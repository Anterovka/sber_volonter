package ru.sbertech.dataspace.security.graphql

import graphql.ExecutionInput
import org.springframework.graphql.server.WebGraphQlInterceptor
import org.springframework.graphql.server.WebGraphQlRequest
import org.springframework.graphql.server.WebGraphQlResponse
import reactor.core.publisher.Mono
import sbp.sbt.dataspacecore.security.utils.SecurityUtils
import java.util.Collections

/**
 * In graphQLContext, sets the security enable flag.
 * Only for a Web calls, not direct [graphql.GraphQL.execute] calls, they aren't intercepted.
 */
class GqlSecurityFlagSetterInterceptor : WebGraphQlInterceptor {
    override fun intercept(
        request: WebGraphQlRequest,
        chain: WebGraphQlInterceptor.Chain,
    ): Mono<WebGraphQlResponse> {
        request.configureExecutionInput { _: ExecutionInput?, builder: ExecutionInput.Builder ->
            builder
                .graphQLContext(
                    Collections.singletonMap<String?, Any>(SecurityUtils.GRAPHQL_CONTEXT_SECURE_ENABLE_NAME, true),
                ).build()
        }
        return chain.next(request)
    }
}
