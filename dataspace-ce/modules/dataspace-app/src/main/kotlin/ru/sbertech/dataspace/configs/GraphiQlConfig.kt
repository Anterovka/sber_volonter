package ru.sbertech.dataspace.configs

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties
import org.springframework.boot.autoconfigure.graphql.servlet.GraphQlWebMvcAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.core.io.ClassPathResource
import org.springframework.core.log.LogMessage
import org.springframework.graphql.server.webmvc.GraphiQlHandler
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.RouterFunctions
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import ru.sbertech.dataspace.entity.ModelMetaInfo
import ru.sbertech.dataspace.modelcontext.configs.DataspaceRequestHandler
import ru.sbertech.dataspace.properties.AppProperties
import ru.sbertech.dataspace.util.DsceConstants
import ru.sbertech.dataspace.util.ModelResolver

@Import(
    GraphQlWebMvcAutoConfiguration::class,
    GraphiQLResourcesConfig::class,
)
@EnableConfigurationProperties(GraphQlProperties::class)
class GraphiQlConfig {
    companion object {
        private val logger: Log = LogFactory.getLog(GraphiQlConfig::class.java)
    }

    @Bean
    fun dspcGraphQlRouterFunction(
        modelMetaInfo: ModelMetaInfo,
        appProperties: AppProperties,
        modelResolver: ModelResolver,
    ): RouterFunction<ServerResponse> {
        logger.info(LogMessage.format("Models GraphQL endpoint HTTP POST /%s", DsceConstants.MODEL_GRAPHQL_PATH))
        val handler = DataspaceRequestHandler(modelMetaInfo, modelResolver)
        val builder = RouterFunctions.route()
        builder.POST(
            DsceConstants.MODEL_GRAPHQL_PATH,
            handler::handle,
        )
        if (appProperties.singleMode) {
            builder.POST(
                "/graphql",
                handler::handle,
            )
        }

        return builder.build()
    }

    @Bean
    fun graphiQlRouterFunction(
        properties: GraphQlProperties,
        @Value("\${graphql.graphiql.cdn.enabled:false}") graphiqlCdnEnabled: Boolean,
    ): RouterFunction<ServerResponse> {
        logger.info(LogMessage.format("GraphiQL endpoint %s", properties.graphiql.path))
        val builder = RouterFunctions.route()
        builder.GET("/loopback") { loopback() }

        if (properties.graphiql.isEnabled) {
            var graphiQLHandler = GraphiQlHandler(properties.path, properties.websocket.path)
            graphiQLHandler = checkIfCdnDisabled(graphiqlCdnEnabled, graphiQLHandler, properties)

            builder.GET(properties.graphiql.path) { request: ServerRequest ->
                graphiQLHandler.handleRequest(
                    request,
                )
            }
        }
        return builder.build()
    }

    @Bean
    fun customCharacterEncodingFilter(): CustomCharacterEncodingFilter = CustomCharacterEncodingFilter()

    private fun checkIfCdnDisabled(
        graphiqlCdnEnabled: Boolean,
        graphiQLHandler: GraphiQlHandler,
        properties: GraphQlProperties,
    ): GraphiQlHandler {
        var graphiQLHandlerChecked = graphiQLHandler
        if (!graphiqlCdnEnabled) {
            val graphiQlPage = ClassPathResource("static/index.html")
            graphiQLHandlerChecked = GraphiQlHandler(properties.path, properties.websocket.path, graphiQlPage)
            logger.info(LogMessage.format("CDN for GraphiQL is disabled. Will be use local GraphiQL instead."))
        }
        return graphiQLHandlerChecked
    }

    private fun loopback(): ServerResponse =
        ServerResponse
            .status(HttpStatus.NOT_FOUND)
            .build()
}
