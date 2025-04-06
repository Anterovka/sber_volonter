package ru.sbertech.dataspace.security.jwt.validator

import org.apache.commons.io.FileUtils
import ru.sbertech.dataspace.security.exception.SecurityConfigException
import ru.sbertech.dataspace.security.jwt.validator.JwtValidator.TokenValidatorSettings
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

class OfflineJwtValidator private constructor(
    settings: TokenValidatorSettings,
) : TokenValidator {
    private val tokenValidator: JwtValidator = JwtValidator(settings)

    override fun validate(token: String) {
        tokenValidator.validate(token)
    }

    companion object {
        fun of(settings: TokenValidatorSettings): OfflineJwtValidator {
            if (settings.jwks.isNullOrEmpty()) {
                throw SecurityConfigException("OfflineTokenValidator. JWKS is mandatory to set")
            }
            return OfflineJwtValidator(settings)
        }

        fun ofPath(
            settings: CommonJwtValidatorSettings?,
            pathToJwks: String,
        ): OfflineJwtValidator {
            // TODO: bad file path test, handling
            val newSettings =
                try {
                    val jwksStr = FileUtils.readFileToString(File(pathToJwks), Charset.defaultCharset())
                    TokenValidatorSettings(settings, jwksStr)
                } catch (e: IOException) {
                    throw SecurityConfigException("Error upon reading JWKS from file: $pathToJwks")
                }
            return OfflineJwtValidator(newSettings)
        }
    }
}
