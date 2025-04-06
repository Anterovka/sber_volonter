package ru.sbertech.dataspace.security

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.owasp.encoder.Encode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import ru.sbertech.dataspace.security.exception.AuthenticationException
import ru.sbertech.dataspace.security.exception.SecurityConfigException
import ru.sbertech.dataspace.security.requestProcessors.IRequestProcessor
import ru.sbertech.dataspace.services.exception.ContextOperationException
import ru.sbertech.dataspace.util.ModelResolver
import sbp.sbt.dataspacecore.security.utils.SecurityUtils
import java.io.IOException

class GlobalFilter(
    private val modelResolver: ModelResolver,
) : Filter {
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(GlobalFilter::class.java)
    }

    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        chain: FilterChain,
    ) {
        try {
            if (handleSecurityIsSuccess(request as HttpServletRequest, response as HttpServletResponse)) {
                chain.doFilter(request, response)
            }
        } finally {
            SecurityUtils.clearContext()
        }
    }

    @Throws(IOException::class)
    private fun handleSecurityIsSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): Boolean {
        try {
            resolveContext(request, response)
        } catch (e: Exception) {
            LOGGER.error("Error encountered within GlobalFilter", e)
            when (e) {
                // TODO: for some reason no error message is visible in tests, just the status
                is AuthenticationException -> {
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), Encode.forHtml(e.message))
                }

                is SecurityConfigException -> {
                    response.sendError(HttpStatus.BAD_REQUEST.value(), Encode.forHtml(e.message))
                }

                else -> {
                    response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), Encode.forHtml(e.message))
                }
            }
            return false
        }
        return true
    }

    private fun resolveContext(
        req: HttpServletRequest,
        resp: HttpServletResponse,
    ) {
        SecurityUtils.clearContext()
        val modelId =
            modelResolver.resolveModelIdNullable(req)
                // TODO make something more meaningful, fake controllers?
                ?: return

        try {
            val context = modelResolver.resolveActiveContext(modelId)

            val requestProcessors = context.getBeansOfType(IRequestProcessor::class.java)
            requestProcessors.values
                .filter { it.isSuitable(req) }
                .sorted()
                .forEach {
                    it.processRequest(req)
                }
        } catch (exception: ContextOperationException) {
            LOGGER.error("Context exception within Global Filter", exception)
        }
    }
}
