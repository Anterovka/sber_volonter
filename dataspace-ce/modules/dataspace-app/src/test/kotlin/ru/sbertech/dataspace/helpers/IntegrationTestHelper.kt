package ru.sbertech.dataspace.helpers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.codec.digest.DigestUtils
import org.assertj.core.api.ThrowingConsumer
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import ru.sbertech.dataspace.configs.ParentCtxConfig
import ru.sbertech.dataspace.entity.ModelMetaInfo
import ru.sbertech.dataspace.entitymanager.selector.Selector
import ru.sbertech.dataspace.universalvalue.accept
import ru.sbertech.dataspace.util.ContextHelper
import java.nio.file.Paths
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.Base64
import java.util.Calendar
import java.util.Collections
import java.util.UUID
import java.util.function.Consumer

@SpringBootTest(
    classes = [ParentCtxConfig::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntegrationTestHelper {
    @LocalServerPort
    protected var localServerPort: Int = 0
    protected lateinit var webClient: WebClient
    protected final val objectMapper: ObjectMapper =
        ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    // Util fields
    private final var jsonNodeReturningVisitor = JsonNodeReturningVisitor(objectMapper)

    // Beans
    @Autowired
    protected lateinit var modelMetaInfo: ModelMetaInfo

    @BeforeAll
    fun setup() {
        webClient =
            WebClient
                .builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .baseUrl("http://127.0.0.1:$localServerPort")
                .exchangeStrategies(
                    ExchangeStrategies
                        .builder()
                        .codecs {
                            it.defaultCodecs().jackson2JsonEncoder(
                                Jackson2JsonEncoder(objectMapper),
                            )
                            it.defaultCodecs().jackson2JsonDecoder(
                                Jackson2JsonDecoder(objectMapper),
                            )
                        }.build(),
                ).build()
    }

    // ////////////////////////////////////
    //
    // POST Methods
    //
    // ////////////////////////////////////

    protected fun post(
        path: String,
        requestJson: JsonNode,
    ): ResponseEntity<JsonNode> = post(path, requestJson, JsonNode::class.java) { _: HttpHeaders -> }

    protected fun post(path: String): ResponseEntity<JsonNode> = post(path, null, JsonNode::class.java) { _: HttpHeaders -> }

    protected fun post(
        path: String,
        requestJson: JsonNode,
        headersConsumer: Consumer<HttpHeaders>,
    ): ResponseEntity<JsonNode> = post(path, requestJson, JsonNode::class.java, headersConsumer)

    protected fun <R> post(
        path: String,
        requestJson: JsonNode?,
        clazz: Class<R>,
        headersConsumer: Consumer<HttpHeaders>,
    ): ResponseEntity<R> {
        val httpHeaders = HttpHeaders()
        headersConsumer.accept(httpHeaders)

        val responseEntity =
            webClient
                .post()
                .uri { uriBuilder -> uriBuilder.path(path).build() }
                .headers { headers -> headers.putAll(httpHeaders) }
                .apply { if (requestJson != null) bodyValue(requestJson) }
                .retrieve()
                .toEntity(clazz)
                .block()

        return responseEntity ?: throw RuntimeException("Response entity is null")
    }

    // ////////////////////////////////////
    //
    // PUT Methods
    //
    // ////////////////////////////////////

    protected fun put(
        path: String,
        requestJson: JsonNode,
    ): ResponseEntity<JsonNode> = put(path, requestJson, JsonNode::class.java) { _: HttpHeaders -> }

    protected fun put(path: String): ResponseEntity<JsonNode> = put(path, null, JsonNode::class.java) { _: HttpHeaders -> }

    protected fun put(
        path: String,
        requestJson: JsonNode,
        headersConsumer: Consumer<HttpHeaders>,
    ): ResponseEntity<JsonNode> = put(path, requestJson, JsonNode::class.java, headersConsumer)

    protected fun <R> put(
        path: String,
        requestJson: JsonNode?,
        clazz: Class<R>,
        headersConsumer: Consumer<HttpHeaders>,
    ): ResponseEntity<R> {
        val httpHeaders = HttpHeaders()
        headersConsumer.accept(httpHeaders)

        val responseEntity =
            webClient
                .put()
                .uri { uriBuilder -> uriBuilder.path(path).build() }
                .headers { headers -> headers.putAll(httpHeaders) }
                .apply { if (requestJson != null) bodyValue(requestJson) }
                .retrieve()
                .toEntity(clazz)
                .block()

        return responseEntity ?: throw RuntimeException("Response entity is null")
    }

    // ////////////////////////////////////
    //
    // DELETE Methods
    //
    // ////////////////////////////////////

    protected fun delete(path: String): ResponseEntity<Void> = delete(path) { _: HttpHeaders -> }

    protected fun delete(
        path: String,
        headersConsumer: Consumer<HttpHeaders>,
    ): ResponseEntity<Void> = delete(path, Void::class.java, headersConsumer)

    protected fun <R> delete(
        path: String,
        clazz: Class<R>,
        headersConsumer: Consumer<HttpHeaders>,
    ): ResponseEntity<R> {
        val httpHeaders = HttpHeaders()
        headersConsumer.accept(httpHeaders)

        val responseEntity =
            webClient
                .delete()
                .uri { uriBuilder -> uriBuilder.path(path).build() }
                .headers { headers -> headers.putAll(httpHeaders) }
                .retrieve()
                .toEntity(clazz)
                .block()

        return responseEntity ?: throw RuntimeException("Response entity is null")
    }

    protected fun <R> delete(
        path: String,
        clazz: Class<R>,
    ): ResponseEntity<R> = delete(path, null, clazz) { _: HttpHeaders -> }

    protected fun delete(
        path: String,
        requestJson: JsonNode,
    ): ResponseEntity<JsonNode> = delete(path, requestJson) { _: HttpHeaders -> }

    protected fun delete(
        path: String,
        requestJson: JsonNode,
        headersConsumer: Consumer<HttpHeaders>,
    ): ResponseEntity<JsonNode> = delete(path, requestJson, JsonNode::class.java, headersConsumer)

    protected fun <R> delete(
        path: String,
        requestJson: JsonNode?,
        clazz: Class<R>,
        headersConsumer: Consumer<HttpHeaders>,
    ): ResponseEntity<R> {
        val httpHeaders = HttpHeaders()
        headersConsumer.accept(httpHeaders)

        val responseEntity =
            webClient
                .method(HttpMethod.DELETE)
                .uri { uriBuilder -> uriBuilder.path(path).build() }
                .headers { headers -> headers.putAll(httpHeaders) }
                .apply { if (requestJson != null) bodyValue(requestJson) }
                .retrieve()
                .toEntity(clazz)
                .block()

        return responseEntity ?: throw RuntimeException("Response entity is null")
    }

    // ////////////////////////////////////
    //
    // GET Methods
    //
    // ////////////////////////////////////

    protected fun get(path: String): ResponseEntity<JsonNode> = get(path) { _: HttpHeaders -> }

    protected fun get(
        path: String,
        headersConsumer: Consumer<HttpHeaders>,
    ): ResponseEntity<JsonNode> = get(path, JsonNode::class.java, mapOf(), headersConsumer)

    protected fun get(
        path: String,
        variables: Map<String, Any?>,
    ): ResponseEntity<JsonNode> = get(path, JsonNode::class.java, variables) { _: HttpHeaders -> }

    protected fun get(
        path: String,
        variables: Map<String, Any?>,
        headersConsumer: Consumer<HttpHeaders>,
    ): ResponseEntity<JsonNode> = get(path, JsonNode::class.java, variables, headersConsumer)

    protected fun <R> get(
        path: String,
        clazz: Class<R>,
        variables: Map<String, Any?>,
        headersConsumer: Consumer<HttpHeaders>,
    ): ResponseEntity<R> {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        headersConsumer.accept(httpHeaders)

        val responseEntity =
            webClient
                .method(HttpMethod.GET)
                .uri { uriBuilder ->
                    uriBuilder
                        .path(path)
                        .apply {
                            variables.forEach { (key, value) ->
                                queryParam(key, value)
                            }
                        }.build()
                }.headers { headers -> headers.putAll(httpHeaders) }
                .retrieve()
                .toEntity(clazz)
                .block()

        return responseEntity ?: throw RuntimeException("Response entity is null")
    }

    // ////////////////////////////////////
    //
    // Util stuff
    //
    // ////////////////////////////////////

    companion object {
        @DynamicPropertySource
        @JvmStatic
        fun configureTestProperties(registry: DynamicPropertyRegistry) {
            val folderPath =
                Paths
                    .get(
                        requireNotNull(
                            IntegrationTestHelper::class.java.classLoader
                                ?.getResource("models")
                                ?.toURI(),
                        ),
                    ).toFile()
                    .absolutePath

            registry.apply {
                add("dataspace.app.pdmZipped") { false }
                add("dataspace.app.pathConfigDirectory") { folderPath }
            }
        }

        /**
         * kid: d88821b6-02e8-470a-95c9-1b003350e760
         *
         * {
         *   "iss": "auth0",
         *   "arrayTest": ["one", "two", "three"],
         *   "iss": "iss1",
         *   "aud": "aud1",
         *   "sub": "12345678901234",
         *   "someInt": 1,
         *   "jwt1UniqueField": "random value",
         *   "s-u_b": "12345678901234",
         *   "obj": {
         *     "inner": "innerValue"
         *   }
         * }
         */
        @JvmStatic
        protected val JWT_FOR_JWKS_FROM_FILE: String =
            @Suppress("ktlint:standard:max-line-length")
            "eyJraWQiOiJkODg4MjFiNi0wMmU4LTQ3MGEtOTVjOS0xYjAwMzM1MGU3NjAiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJhdWQxIiwic3ViIjoiMTIzNDU2Nzg5MDEyMzQiLCJzLXVfYiI6IjEyMzQ1Njc4OTAxMjM0Iiwib2JqIjp7ImlubmVyIjoiaW5uZXJWYWx1ZSJ9LCJpc3MiOiJpc3MxIiwiYXJyYXlUZXN0IjpbIm9uZSIsInR3byIsInRocmVlIl0sImp3dDFVbmlxdWVGaWVsZCI6InJhbmRvbSB2YWx1ZSIsImV4cCI6NTUyMTk1NzI1MCwic29tZUludCI6MX0.T6KQt9E1E9wmAUIXxs5zi4v-9S4xYEhOFeYjKWw4iYib-t67uNR96RikP5OHTn1Y1UIRY6Q7893sqiHhj1Pz9irIF70k8q-MQb5iKdVgg_1Asb49cXd1sH2ez2GjSWQKTdv0mzKYK831tFemcZVQwS3wX7BWXKUMPco5RvqtxYA"

        /**
         * kid: d88821b6-02e8-470a-95c9-1b003350e760
         *
         * {
         *   "iss": "auth0",
         *   "arrayTest": ["one", "two", "three"],
         *   "iss": "iss1",
         *   "aud": "aud1",
         *   "sub": "12345678901234",
         *   "someInt": 1,
         *   "jwt1UniqueField": "random value",
         *   "s-u_b": "12345678901234",
         *   "obj": {
         *     "inner": "innerValue"
         *   }
         * }
         */
        @JvmStatic
        protected val OUTDATED_JWT_FOR_JWKS_FROM_FILE: String =
            @Suppress("ktlint:standard:max-line-length")
            "eyJraWQiOiJkODg4MjFiNi0wMmU4LTQ3MGEtOTVjOS0xYjAwMzM1MGU3NjAiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJhdWQxIiwic3ViIjoiMTIzNDU2Nzg5MDEyMzQiLCJzLXVfYiI6IjEyMzQ1Njc4OTAxMjM0Iiwib2JqIjp7ImlubmVyIjoiaW5uZXJWYWx1ZSJ9LCJpc3MiOiJpc3MxIiwiYXJyYXlUZXN0IjpbIm9uZSIsInR3byIsInRocmVlIl0sImp3dDFVbmlxdWVGaWVsZCI6InJhbmRvbSB2YWx1ZSIsImV4cCI6MTczNTEzMTA1MCwic29tZUludCI6MX0.JCfkjz9jB2Rj72KrUOjALUfmoCv3Kk8meA-T19B-aDehwZwwto3n9usgHq7aiL3lcNu2fTiYJytqjz58R-ig5oLFKnQhLNuBjVME-M34o41rrrxBk7ar6KYEbNix6SXmSYHwdCTxDzcwXZ7rc7zGI-4x1wZe2hPG7UjDAWEAVhU"

        @JvmStatic
        protected val JWK: JsonNode

        @JvmStatic
        protected val JWT_CREATED_1: String

        @JvmStatic
        protected val JWT_CREATED_2: String

        @JvmStatic
        protected val JWT_CREATED_MULTI_AUD: String

        @JvmStatic
        protected val OUTDATED_JWT_CREATED_1: String

        init {
            val keyGen = KeyPairGenerator.getInstance("RSA")
            keyGen.initialize(1024)
            val keyPair1 = keyGen.generateKeyPair()
            val priv = keyPair1.private
            val pub = keyPair1.public

            val randomString = UUID.randomUUID().toString()

            val cal = Calendar.getInstance()
            cal.add(Calendar.MINUTE, 120)

            val cal2 = Calendar.getInstance()
            cal2.add(Calendar.MINUTE, -10)

            val algorithm: Algorithm = Algorithm.RSA256(pub as RSAPublicKey, priv as RSAPrivateKey)
            JWT_CREATED_1 = createJwt1(cal, randomString, algorithm)
            JWT_CREATED_2 =
                JWT
                    .create()
                    .withIssuer("iss1")
                    .withClaim("arrayTest", mutableListOf("four", "five", "six"))
                    .withClaim("aud", "aud1")
                    .withClaim("sub", "9876543210")
                    .withClaim("s-u_b", "9876543210")
                    .withClaim("someInt", 2)
                    .withExpiresAt(cal.time)
                    .withClaim(
                        "obj",
                        HashMap<String, String>().apply {
                            this["inner"] = "innerValue"
                            this["odtMsc"] = "2023-11-14T17:23:59.783+03:00"
                        },
                    ).withClaim(
                        "nested_obj",
                        java.util.Map.of<String, Map<String, List<String>>>(
                            "some_good_field",
                            java.util.Map.of<String, List<String>>(
                                "roles",
                                mutableListOf("admin", "beb"),
                            ),
                            "some_other_field",
                            java.util.Map.of<String, List<String>>(
                                "roles",
                                mutableListOf("other_admin", "beb"),
                            ),
                        ),
                    ).withKeyId(randomString)
                    .sign(algorithm)
            OUTDATED_JWT_CREATED_1 = createJwt1(cal2, randomString, algorithm)
            JWT_CREATED_MULTI_AUD =
                JWT
                    .create()
                    .withIssuer("auth0")
                    .withClaim("aud", mutableListOf("aud1", "aud2", "aud3"))
                    .withClaim("sub", "9876543210")
                    .withExpiresAt(cal.time)
                    .withKeyId(randomString)
                    .sign(algorithm)
            val stringObjectMap = generateJWK(pub, randomString)

            val keyPair2 = keyGen.generateKeyPair()
            val pub2 = keyPair2.public
            val stringObjectMap2 = generateJWK(pub2, UUID.randomUUID().toString())

            JWK =
                ObjectMapper().valueToTree(
                    Collections.singletonMap(
                        "keys",
                        listOf(stringObjectMap, stringObjectMap2),
                    ),
                )
        }

        private fun createJwt1(
            cal: Calendar,
            kid: String,
            algorithm: Algorithm,
        ): String =
            JWT
                .create()
                .withIssuer("auth0")
                .withClaim("arrayTest", mutableListOf("one", "two", "three"))
                .withClaim("iss", "iss1")
                .withClaim("aud", "aud1")
                .withClaim("sub", "12345678901234")
                .withClaim("someInt", 1)
                .withClaim("jwt1UniqueField", "random value")
                .withClaim("s-u_b", "12345678901234")
                .withClaim("obj", Collections.singletonMap("inner", "innerValue"))
                .withExpiresAt(cal.time)
                .withKeyId(kid)
                .sign(algorithm)

        private fun generateJWK(
            publicKey: PublicKey,
            kid: String,
        ): Map<String, Any> {
            val rsa = publicKey as RSAPublicKey

            val values: MutableMap<String, Any> = HashMap()

            values["kty"] = rsa.algorithm // getAlgorithm() returns kty not algorithm
            values["kid"] = kid
            values["n"] = Base64.getUrlEncoder().encodeToString(rsa.modulus.toByteArray())
            values["e"] = Base64.getUrlEncoder().encodeToString(rsa.publicExponent.toByteArray())
            values["alg"] = "RS256"
            values["use"] = "sig"

            return values
        }
    }

    protected fun getRandomString(): String = "p" + DigestUtils.sha256Hex(UUID.randomUUID().toString()) + "p"

    protected fun <T> getChildBean(
        bean: Class<T>,
        modelId: String,
    ): T =
        requireNotNull(
            modelMetaInfo
                .getModelByModelId(modelId)
                .containersInfo
                ?.get(0)
                ?.context
                ?.getBean(bean),
        ) { "Bean $bean not found" }

    protected fun assertSelect(
        modelId: String,
        selector: Selector,
        vararg assertions: ThrowingConsumer<in JsonNode>,
    ) {
        getChildBean(ContextHelper::class.java, modelId)
            .withEntityManagerContext {
                entityManager
                    .select(selector)!!
                    .accept(jsonNodeReturningVisitor)
                    .run {
                        assertions.forEach { it.accept(this) }
                    }
            }
    }
}
