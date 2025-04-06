package ru.sbertech.dataspace.util

import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.web.servlet.function.ServerRequest
import ru.sbertech.dataspace.entity.ModelInfo
import ru.sbertech.dataspace.entity.ModelMetaInfo

class ModelResolver(
    private val singleMode: Boolean,
    private val defaultModelId: String,
    private val modelMetaInfo: ModelMetaInfo,
    // If singleMode -> id is constant, otherwise it gets extracted from request's path
    private val idResolver: IdResolver = createIdResolver(singleMode, defaultModelId),
) {
    interface IdResolver {
        fun resolveId(serverRequest: ServerRequest): String

        fun resolveIdNullable(serverRequest: ServerRequest): String?

        fun resolveId(servletRequest: HttpServletRequest): String

        fun resolveIdNullable(servletRequest: HttpServletRequest): String?
    }

    //
    // ID
    //

    fun resolveModelId(serverRequest: ServerRequest): String = idResolver.resolveId(serverRequest)

    fun resolveModelIdNullable(serverRequest: ServerRequest): String? = idResolver.resolveIdNullable(serverRequest)

    fun resolveModelId(servletRequest: HttpServletRequest): String = idResolver.resolveId(servletRequest)

    fun resolveModelIdNullable(servletRequest: HttpServletRequest): String? = idResolver.resolveIdNullable(servletRequest)

    //
    // Model
    //

    fun resolveModel(modelId: String): ModelInfo = modelMetaInfo.getModelByModelId(modelId)

    fun resolveModelNullable(modelId: String): ModelInfo? = modelMetaInfo.searchModelByModelId(modelId)

    fun resolveModel(serverRequest: ServerRequest): ModelInfo = modelMetaInfo.getModelByModelId(resolveModelId(serverRequest))

    fun resolveModelNullable(serverRequest: ServerRequest): ModelInfo? = modelMetaInfo.searchModelByModelId(resolveModelId(serverRequest))

    fun resolveModel(servletRequest: HttpServletRequest): ModelInfo = modelMetaInfo.getModelByModelId(resolveModelId(servletRequest))

    fun resolveModelNullable(servletRequest: HttpServletRequest): ModelInfo? =
        modelMetaInfo.searchModelByModelId(resolveModelId(servletRequest))

    //
    // Context
    //

    fun resolveActiveContext(modelId: String): ConfigurableApplicationContext = modelMetaInfo.getActiveContextByModelId(modelId)

    fun resolveActiveContextNullable(modelId: String): ConfigurableApplicationContext? = modelMetaInfo.searchActiveContextByModelId(modelId)

    fun resolveActiveContext(serverRequest: ServerRequest): ConfigurableApplicationContext =
        modelMetaInfo.getActiveContextByModelId(resolveModelId(serverRequest))

    fun resolveActiveContextNullable(serverRequest: ServerRequest): ConfigurableApplicationContext? =
        modelMetaInfo.searchActiveContextByModelId(resolveModelId(serverRequest))

    fun resolveActiveContext(servletRequest: HttpServletRequest): ConfigurableApplicationContext =
        modelMetaInfo.getActiveContextByModelId(resolveModelId(servletRequest))

    fun resolveActiveContextNullable(servletRequest: HttpServletRequest): ConfigurableApplicationContext? =
        modelMetaInfo.searchActiveContextByModelId(resolveModelId(servletRequest))

    companion object {
        private fun createIdResolver(
            singleMode: Boolean,
            defaultModelId: String,
        ): IdResolver =
            if (singleMode) {
                // Constant resolver
                object : IdResolver {
                    override fun resolveId(serverRequest: ServerRequest): String = defaultModelId

                    override fun resolveIdNullable(serverRequest: ServerRequest): String = defaultModelId

                    override fun resolveId(servletRequest: HttpServletRequest): String = defaultModelId

                    override fun resolveIdNullable(servletRequest: HttpServletRequest): String = defaultModelId
                }
            } else {
                // Path resolver
                object : IdResolver {
                    override fun resolveId(serverRequest: ServerRequest): String =
                        serverRequest.findModelId() ?: throw IllegalArgumentException("Model id not found")

                    override fun resolveIdNullable(serverRequest: ServerRequest): String? = serverRequest.findModelId()

                    override fun resolveId(servletRequest: HttpServletRequest): String =
                        servletRequest.findModelId() ?: throw IllegalArgumentException("Model id not found")

                    override fun resolveIdNullable(servletRequest: HttpServletRequest): String? = servletRequest.findModelId()
                }
            }
    }
}
