package ru.sbertech.dataspace.modelcontext.configs

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import io.micrometer.core.instrument.DistributionSummary
import io.micrometer.core.instrument.Metrics
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.graphql.server.webmvc.GraphQlHttpHandler
import org.springframework.http.HttpStatusCode
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import ru.sbertech.dataspace.entity.ModelMetaInfo
import ru.sbertech.dataspace.util.ModelResolver
import java.time.Duration

class DataspaceRequestHandler(
    private val modelMetaInfo: ModelMetaInfo,
    private val modelResolver: ModelResolver,
) {
    fun handle(request: ServerRequest): ServerResponse {
        val startTimeMs = System.currentTimeMillis()
        val modelId = modelResolver.resolveModelId(request)

        modelMetaInfo.upHttpRequestCount(modelId)
        log.debug("Has been received request with modelId=$modelId")

        val context = modelResolver.resolveActiveContextNullable(modelId)
        if (context == null) {
            val errMessage =
                "No context was found for modelId=$modelId. Probably there is no such model."
            log.warn(errMessage)
            val response =
                ServerResponse.status(HttpStatusCode.valueOf(400)).body(getErrorJsonMessage(errMessage))
            gatherMetrics(request, response, startTimeMs, modelId)
            return response
        }

        val httpHandler = context.getBean("childGraphQlHttpHandler", GraphQlHttpHandler::class)
        val response = (httpHandler as GraphQlHttpHandler).handleRequest(request)

        gatherMetrics(request, response, startTimeMs, modelId)
        modelMetaInfo.upHttpResponseCount(modelId)
        return response
    }

    private fun getErrorJsonMessage(message: String): String {
        // as Array
        val mapper = ObjectMapper()

        val rootNode: ObjectNode = mapper.createObjectNode()
        val errorsNode = rootNode.withArray("errors")
        val messageNode: ObjectNode = mapper.createObjectNode()
        messageNode.put("message", message)
        errorsNode.add(messageNode)
        val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode)
        return json
    }

    private fun gatherMetrics(
        request: ServerRequest,
        response: ServerResponse,
        startTimeMs: Long,
        modelId: String,
    ) {
        DistributionSummary
            .builder("http.server.requests.models.seconds")
            .baseUnit("seconds")
            .description("Request duration to specific model in seconds")
            .distributionStatisticExpiry(Duration.ofSeconds(60))
            .publishPercentiles(0.95, 0.99)
            .tag("modelId", modelId)
            .tag("status", response.statusCode().value().toString())
            .register(Metrics.globalRegistry)
            .run { record(((System.currentTimeMillis() - startTimeMs) / 1000.0) % 60) }
    }

    companion object {
        private val log: Log = LogFactory.getLog(DataspaceRequestHandler::class.java)
    }
}
