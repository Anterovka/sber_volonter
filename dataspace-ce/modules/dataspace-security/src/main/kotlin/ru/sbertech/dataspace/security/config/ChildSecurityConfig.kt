package ru.sbertech.dataspace.security.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.util.StringUtils
import ru.sbertech.dataspace.security.config.JwksConfig.JwksSource.DB
import ru.sbertech.dataspace.security.config.JwksConfig.JwksSource.FILE
import ru.sbertech.dataspace.security.config.JwksConfig.JwksSource.IAM
import ru.sbertech.dataspace.security.config.JwksConfig.JwksSource.KEYCLOAK
import ru.sbertech.dataspace.security.config.JwksConfig.JwksSource.PROPERTY
import ru.sbertech.dataspace.security.config.JwksConfig.JwksSource.URL
import ru.sbertech.dataspace.security.exception.SecurityConfigException
import ru.sbertech.dataspace.security.jwt.validator.DatabaseJwtValidator
import ru.sbertech.dataspace.security.jwt.validator.JwtValidator.TokenValidatorSettings
import ru.sbertech.dataspace.security.jwt.validator.OfflineJwtValidator
import ru.sbertech.dataspace.security.jwt.validator.OnlineJwtValidator
import ru.sbertech.dataspace.security.jwt.validator.TokenValidator
import ru.sbertech.dataspace.security.requestProcessors.JwtRequestProcessor
import ru.sbertech.dataspace.security.requestProcessors.SetRequestToContextRequestProcessor
import ru.sbertech.dataspace.security.requestProcessors.util.Endpoint
import ru.sbertech.dataspace.util.isNotNullOrEmpty
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson
import java.nio.file.FileSystems

@Import(
    value = [
        GraphQlSecurityAdminConfig::class,
    ],
)
class ChildSecurityConfig {
    @Bean
    fun setRequestToContextRequestProcessor(): SetRequestToContextRequestProcessor = SetRequestToContextRequestProcessor()

    @Bean
    @ConfigurationProperties(prefix = "dataspace.security")
    fun securityConfig(): SecurityConfigurationProperties = SecurityConfigurationProperties()

    /** Проверяет, что jwks указан явно, при использовании jwt-only порта  */
    @Value("\${dataspace.security.graphql.permissions.source:#{null}}")
    fun checkJwtOnlyAndJwks(
        @Value("\${dataspace.security.graphql.permissions.source:#{null}}") permissionsSource: String?,
        @Value("\${dataspace.security.jwks.source:#{null}}") jwksSource: String?,
        @Value("\${dataspace.security.jwt.validation.disable:#{null}}") jwtValidationDisabled: Boolean?,
    ) {
        if (permissionsSource.isNotNullOrEmpty() && jwksSource.isNullOrEmpty() && java.lang.Boolean.TRUE != jwtValidationDisabled) {
            throw SecurityConfigException(
                "При использовании dataspace.security.graphql.permissions.source необходимо явно указать источник jwks," +
                    " либо выставить флаг dataspace.security.jwt.validation.disable",
            )
        }
    }

    @Bean
    @ConditionalOnExpression("'\${dataspace.security.jwks.source:}' != ''")
    fun businessJwtValidator(
        securityConfig: SecurityConfigurationProperties,
        entitiesReadAccessJson: EntitiesReadAccessJson,
        @Value("\${child.model.path:model-path}") modelPath: String,
    ): TokenValidator {
        val jwksSource: JwksConfig.JwksSource = securityConfig.jwks.source!!
        val jwksValue: String? = securityConfig.jwks.value
        val settings: TokenValidatorSettings = propsToTokenValidatorSettings(securityConfig)
        return when (jwksSource) {
            PROPERTY -> {
                OfflineJwtValidator.of(settings)
            }
            FILE -> {
                OfflineJwtValidator.ofPath(settings, modelPath + FileSystems.getDefault().separator + JWKS_PATH)
            }
            DB -> {
                DatabaseJwtValidator(entitiesReadAccessJson)
            }
            IAM, URL, KEYCLOAK -> {
                OnlineJwtValidator(
                    settings,
                    requireNotNull(jwksValue) {
                        "JWKS Value for source $jwksSource is required (see dataspace.security.jwks.value)"
                    },
                )
            }
        }
    }

    private fun propsToTokenValidatorSettings(securityConfig: SecurityConfigurationProperties): TokenValidatorSettings {
        val jwksSource: JwksConfig.JwksSource = securityConfig.jwks.source!!
        var settings: TokenValidatorSettings? = null
        // TODO: npe if DB source
        if (jwksSource === PROPERTY || jwksSource === FILE || jwksSource === IAM || jwksSource === URL || jwksSource === KEYCLOAK) {
            settings = TokenValidatorSettings()
            val jwtConfig = securityConfig.jwt
            if (!StringUtils.isEmpty(jwtConfig.iss)) {
                settings.iss = jwtConfig.iss
            }
            if (!StringUtils.isEmpty(jwtConfig.aud)) {
                settings.aud = jwtConfig.aud
            }
            if (jwtConfig.expDelta != null) {
                settings.expDelta = jwtConfig.expDelta ?: 0
            }
            if (jwtConfig.nbfDelta != null) {
                settings.nbfDelta = jwtConfig.nbfDelta ?: 0
            }
        }
        if (jwksSource === PROPERTY) {
            settings!!.jwks = securityConfig.jwks.value
        }
        return settings!!
    }

    @Bean
    @ConditionalOnExpression("'\${dataspace.security.graphql.permissions.source:}' != ''")
    fun secureEndpoints(): SecureEndpoints {
        val secureEndpoints = SecureEndpoints()
        secureEndpoints.add(Endpoint.GRAPHQL)
        secureEndpoints.add(Endpoint.GRAPHQL_SUBSCRIPTION)
        return secureEndpoints
    }

    @Bean
    @ConditionalOnExpression("'\${dataspace.security.graphql.permissions.source:}' != ''")
    fun jwtRequestProcessor(
        securityConfig: SecurityConfigurationProperties,
        secureEndpoints: SecureEndpoints,
        @Qualifier("businessJwtValidator") tokenValidator: TokenValidator?,
    ): JwtRequestProcessor = JwtRequestProcessor(securityConfig, secureEndpoints, tokenValidator)

    companion object {
        const val JWKS_PATH: String = "jwks.json"
    }
}
