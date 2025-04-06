package ru.sbertech.dataspace.security.requestProcessors

import jakarta.servlet.http.HttpServletRequest
import ru.sbertech.dataspace.security.config.SecureEndpoints
import ru.sbertech.dataspace.security.config.SecurityConfigurationProperties
import ru.sbertech.dataspace.security.exception.AuthenticationException
import ru.sbertech.dataspace.security.jwt.validator.TokenValidator
import ru.sbertech.dataspace.security.requestProcessors.util.Endpoint
import ru.sbertech.dataspace.security.token.RequestHeaders
import sbp.sbt.dataspacecore.security.common.DataspaceAuthenticationToken
import sbp.sbt.dataspacecore.security.utils.JwtHelper
import sbp.sbt.dataspacecore.security.utils.SecurityUtils
import java.io.IOException

/** Если присутствует в контексте, то выполняется на всех запросах.
 * При этом если в запросе есть Authorization значение, то валидирует его.
 * Если jwt выставлен обязательным, то проверяет его обязательность только на secureEndpoints */
class JwtRequestProcessor(
    private val secConfig: SecurityConfigurationProperties,
    private val secureEndpoints: SecureEndpoints,
    // не обязательный
    private val tokenValidator: TokenValidator?,
) : IRequestProcessor {
    override fun isSuitable(req: HttpServletRequest): Boolean = true

    override fun processRequest(req: HttpServletRequest) {
        val jwt = req.getHeader(RequestHeaders.JWT.headerName)

        if (jwt == null) {
            // Если jwt отсутствует.
            // Проверяем endpoint. Если endpoint входит в secureEndpoints и jwt обязателен,
            // то ошибка, иначе анонимный доступ.
            val endpoint = Endpoint.byPath(req.requestURI)
            val isSecureEndpoint = secureEndpoints.contains(endpoint)
            if (secConfig.jwt.required && isSecureEndpoint) {
                throw AuthenticationException(
                    "Заголовок " + RequestHeaders.JWT.headerName +
                        " обязателен для заполнения",
                )
            } else {
                DataspaceAuthenticationToken()
            }
        } else {
            // Если JWT есть.
            // Если верификация JWT не отключена, то верифицируем jwt.
            if (!secConfig.jwt.validation.isDisable!!) {
                if (tokenValidator == null) {
                    throw AuthenticationException("Включена валидация JWT, но не предоставлен валидатор")
                }
                tokenValidator.validate(jwt)
            }
            jwtToToken(jwt).also {
                it.isAuthenticated = true
            }
        }.also {
//        it.identifierForNonSecurity = UserInfoHelper.getUserInfo(req, isUserInfoMandatory, kindResolvers)
            it.request = req
            SecurityUtils.setToken(it)
        }
    }

    private fun jwtToToken(jwt: String): DataspaceAuthenticationToken {
        // необходимо вычитать JWT из заголовка
        val allPayloadFields: Map<String, String> =
            try {
                JwtHelper.parseJwt(jwt)
            } catch (ex: IOException) {
                throw AuthenticationException("Ошибка безопасности", ex)
            }
        val token = SecurityUtils.getOrCreateCurrentToken()
        token.attributes = allPayloadFields as MutableMap<String, String>
        token.token = jwt
        return token
    }
}
