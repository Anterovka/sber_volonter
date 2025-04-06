package ru.sbertech.dataspace.security.jwt.validator

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.util.StringUtils
import org.springframework.web.reactive.function.client.WebClient
import ru.sbertech.dataspace.security.jwt.validator.JwtValidator.TokenValidatorSettings

class OnlineJwtValidator(
    settings: CommonJwtValidatorSettings,
    jwksEndpoint: String,
) : DynamicJwtValidator() {
    private val jwksEndpoint: String
    private val settings: CommonJwtValidatorSettings

    init {
        if (jwksEndpoint.isEmpty()) {
            // TODO переделать на SecurityConfigException
            throw SecurityException("OnlineTokenValidator. URL to jwks source is mandatory to set")
        }
        this.settings = settings
        this.jwksEndpoint = jwksEndpoint
    }

    @PostConstruct
    public override fun recreate() {
        var jwksStr: String? = null
        try {
            var webClient =
                WebClient
                    .builder()
                    .build()

            jwksStr =
                webClient
                    .get()
                    .uri(jwksEndpoint)
                    .retrieve()
                    .bodyToMono(String::class.java)
//                .onErrorResume { e -> Mono.empty() }
                    .block()

            if (!StringUtils.hasLength(jwksStr)) {
                LOGGER.error("Не удалось прочитать JWKS у Keycloak`а по адресу {}", jwksEndpoint)
            }
        } catch (e: Exception) {
            LOGGER.warn("Error while try to recreate OnlineTokenValidator", e)
        }

        if (StringUtils.hasLength(jwksStr)) {
            val newSettings = TokenValidatorSettings(settings, jwksStr)
            tokenValidator = JwtValidator(newSettings)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(OnlineJwtValidator::class.java)
    }
}
