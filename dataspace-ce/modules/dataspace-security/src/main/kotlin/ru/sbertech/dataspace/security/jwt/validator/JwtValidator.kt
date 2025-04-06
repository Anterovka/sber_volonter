package ru.sbertech.dataspace.security.jwt.validator

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSVerifier
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.SignedJWT
import ru.sbertech.dataspace.security.exception.AuthenticationException
import ru.sbertech.dataspace.security.exception.SecurityConfigException
import ru.sbertech.dataspace.security.token.JwtEntity
import java.io.IOException
import java.text.ParseException
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class JwtValidator(
    settings: TokenValidatorSettings,
) : TokenValidator {
    companion object {
        // TODO move constants to SecurityUtils
        const val EXP = "exp"
        const val ISS = "iss"
        const val AUD = "aud"
        const val NBF = "nbf"
        const val JWKS = "jwks"
        const val EXP_DELTA = "expDelta"
        const val NBF_DELTA = "nbfDelta"
    }

    var aud: String? = null
    var iss: String? = null

    /** Сколько секунд токен еще считается валидным после истечения exp  */
    var expDelta: Long

    /** Сколько секунд токен уже считается валидным до наступления nbf (нивилирование рассинхрона по времени)  */
    var nbfDelta: Long

    /** keyId -> key  */
    private var parsedJWKS: MutableMap<String, String>? = null
    private val objectMapper: ObjectMapper?

    class TokenValidatorSettings : CommonJwtValidatorSettings {
        var jwks: String? = null

        constructor() {}
        constructor(iss: String?, aud: String?, jwks: String?, expDelta: Long?, nbfDelata: Long?) {
            this.iss = iss
            this.aud = aud
            this.jwks = jwks
            this.expDelta = expDelta!!
            nbfDelta = nbfDelata!!
        }

        constructor(innerSettings: CommonJwtValidatorSettings?, jwks: String?) : super(innerSettings!!) {
            this.jwks = jwks
        }

        fun copy(): TokenValidatorSettings = TokenValidatorSettings(iss, aud, jwks, expDelta, nbfDelta)
    }

    init {
        if (!settings.jwks.isNullOrEmpty()) {
            try {
                installJWKS(settings.jwks)
            } catch (e: IOException) {
                throw SecurityConfigException("JWKS parsing error. JWKS: " + settings.jwks, e)
            }
        }
        if (!settings.aud.isNullOrEmpty()) {
            aud = settings.aud
        }
        if (!settings.iss.isNullOrEmpty()) {
            iss = settings.iss
        }
        if (settings.expDelta < 0) {
            throw SecurityConfigException("expDelta must be greater or equals then 0")
        }
        expDelta = settings.expDelta
        if (settings.nbfDelta < 0) {
            throw SecurityConfigException("nbfDelta must be greater or equals then 0")
        }
        nbfDelta = settings.nbfDelta
        objectMapper = if (settings.objectMapper != null) settings.objectMapper else ObjectMapper()
    }

    override fun validate(token: String) {
        if (token == null) {
            throw AuthenticationException("JWT is empty")
        }
        val unBearerD = token.substring(JwtEntity.JWT_PREFIX.length)
        try {
            if (parsedJWKS == null) {
                throw SecurityConfigException("JWKS is not configured")
            }
            val jwt = SignedJWT.parse(unBearerD)
            val jwk = parsedJWKS!![jwt.header.keyID] ?: throw AuthenticationException("kid JWT not found in JWKS")
            val parse = RSAKey.parse(jwk)
            val verifier: JWSVerifier = RSASSAVerifier(parse)
            if (!jwt.verify(verifier)) {
                throw AuthenticationException("Invalid JWT signature")
            }
            checkIss(jwt)
            checkExp(jwt)
            checkAud(jwt)
            checkNbf(jwt)
        } catch (ex: ParseException) {
            throw AuthenticationException("Security Error. Could not read JWT", ex)
        } catch (ex: JOSEException) {
            throw AuthenticationException("Security Error. Could not read JWT", ex)
        }
    }

    private fun checkExp(jwt: SignedJWT) {
        val jwtExp =
            jwt.payload.toJSONObject()[EXP]
                ?: throw AuthenticationException("JWT has no exp field")
        var currentDate = LocalDateTime.now(ZoneId.of("UTC"))
        if (expDelta > 0) {
            currentDate = currentDate.plusSeconds(expDelta)
        }
        val tokenExpirationDate = LocalDateTime.ofEpochSecond((jwtExp as Long), 0, ZoneOffset.UTC)
        if (currentDate.compareTo(tokenExpirationDate) > 0) {
            throw AuthenticationException("Security Error. JWT is expired")
        }
    }

    private fun checkNbf(jwt: SignedJWT) {
        val jwtNbf =
            jwt.payload.toJSONObject()[NBF]
                ?: // nbf может не быть в валидном jwt
                return
        var currentDate = LocalDateTime.now(ZoneId.of("UTC"))
        if (nbfDelta > 0) {
            currentDate = currentDate.minusSeconds(nbfDelta)
        }
        val tokenExpirationDate = LocalDateTime.ofEpochSecond((jwtNbf as Long), 0, ZoneOffset.UTC)
        if (currentDate < tokenExpirationDate) {
            throw AuthenticationException("Security Error. JWT is not active yet")
        }
    }

    private fun checkAud(jwt: SignedJWT) {
        if (aud == null) {
            return
        }
        val jwtAud =
            jwt.payload.toJSONObject()[AUD]
                ?: throw AuthenticationException("JWT has no aud field")
        if (aud != jwtAud) {
            throw AuthenticationException("Security Error. JWT aud does not match configured one")
        }
    }

    private fun checkIss(jwt: SignedJWT) {
        // Вообще проверка iss кажется излишней, т.к. если jwt пройдет валидацию по JWKS, то и iss должен быть в порядке,
        // т.к. ключи разных keycloack не должны пересекаться.
        val jwtIss =
            jwt.payload.toJSONObject()[ISS]
                ?: throw AuthenticationException("Security Error. JWT does not have iss field")
        if (iss != null && jwtIss != iss) {
            throw AuthenticationException("Security Error. JWT iss does not match configured one")
        }
    }

    @Throws(IOException::class)
    private fun installJWKS(jwksString: String?) {
        val objectMapper = ObjectMapper()
        val jwks = objectMapper.readTree(jwksString)
        setJWKS(jwks)
    }

    fun installJWKS(jwks: JsonNode) {
        setJWKS(jwks)
    }

    fun clearJWKS() {
        parsedJWKS!!.clear()
    }

    private fun setJWKS(jwks: JsonNode) {
        parsedJWKS = HashMap()
        if (jwks.has("keys")) {
            for (keyNode in jwks["keys"]) {
                val kid = keyNode["kid"].asText()
                parsedJWKS!![kid] = keyNode.toString()
            }
        } else {
            val kid = jwks["kid"].asText()
            parsedJWKS!![kid] = jwks.toString()
        }
    }
}
