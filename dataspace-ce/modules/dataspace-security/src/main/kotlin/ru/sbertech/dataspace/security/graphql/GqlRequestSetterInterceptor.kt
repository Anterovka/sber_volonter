package ru.sbertech.dataspace.security.graphql

import graphql.ExecutionInput
import org.springframework.graphql.server.WebGraphQlInterceptor
import org.springframework.graphql.server.WebGraphQlRequest
import org.springframework.graphql.server.WebGraphQlResponse
import reactor.core.publisher.Mono
import sbp.sbt.dataspacecore.security.utils.SecurityUtils
import java.util.Collections

/**
 * Put HttpServletRequest in graphQLContext
 */
class GqlRequestSetterInterceptor : WebGraphQlInterceptor {
    override fun intercept(
        request: WebGraphQlRequest,
        chain: WebGraphQlInterceptor.Chain,
    ): Mono<WebGraphQlResponse> {
        val currentToken = SecurityUtils.getCurrentToken()
        if (currentToken.request != null) {
            request.configureExecutionInput { _: ExecutionInput?, builder: ExecutionInput.Builder ->
                builder
                    .graphQLContext(
                        Collections.singletonMap<String?, Any>(SecurityUtils.GRAPHQL_CONTEXT_REQEUST_NAME, currentToken.request),
                    ).build()
            }
        }
        return chain.next(request)
    }
}
