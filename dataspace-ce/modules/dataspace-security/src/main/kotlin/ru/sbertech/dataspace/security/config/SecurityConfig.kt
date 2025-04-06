package ru.sbertech.dataspace.security.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.util.StringUtils
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import ru.sbertech.dataspace.security.GlobalFilter
import ru.sbertech.dataspace.security.admin.GraphQlSecurityAdminHandler
import ru.sbertech.dataspace.security.requestProcessors.util.Endpoint
import ru.sbertech.dataspace.util.ModelResolver

class SecurityConfig {
    @Bean
    @ConditionalOnMissingBean(GlobalFilter::class)
    fun globalFilter(
        modelResolver: ModelResolver,
        @Value("\${graphql.url:graphql}") graphqlUrl: String,
        @Value("\${security.endpoint.graphql.url:}") graphQlSecurityEndpointUrl: String,
    ): GlobalFilter {
        val resultUrl = if (!StringUtils.hasLength(graphQlSecurityEndpointUrl)) graphqlUrl else graphQlSecurityEndpointUrl
        Endpoint.GRAPHQL.changeURI(if (resultUrl[0] != '/') "/$resultUrl" else resultUrl)
        return GlobalFilter(modelResolver)
    }

    @Bean
    fun graphQlSecurityAdminHandler(modelResolver: ModelResolver): RouterFunction<ServerResponse> =
        GraphQlSecurityAdminHandler.initialize(modelResolver)
}
