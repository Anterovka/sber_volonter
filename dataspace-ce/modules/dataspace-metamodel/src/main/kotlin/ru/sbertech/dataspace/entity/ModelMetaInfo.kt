package ru.sbertech.dataspace.entity

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ConfigurableApplicationContext
import ru.sbertech.dataspace.services.exception.ContextOperationException
import java.util.concurrent.locks.ReentrantLock

class ModelMetaInfo {
    private val lock = ReentrantLock()

    private var modelsInfo: MutableList<ModelInfo> = ArrayList()

    fun addModelInfo(modelInfo: ModelInfo) {
        modelsInfo.add(modelInfo)
    }

    fun removeModelInfo(modelInfo: ModelInfo) {
        modelsInfo.remove(modelInfo)
    }

    val allModels: List<ModelInfo>
        get() = modelsInfo

    fun removeModelByModelId(modelId: String?) {
        val modelInfoOptional =
            modelsInfo
                .stream()
                .filter { modelInfo: ModelInfo ->
                    modelInfo.modelId == modelId
                }.findFirst()
        if (modelInfoOptional.isPresent) {
            LOGGER.info("The model {} will be removed from the list of models", modelInfoOptional.get().modelId)
            modelsInfo.remove(modelInfoOptional.get())
        }
    }

    fun searchModelByModelId(modelId: String): ModelInfo? =
        modelsInfo
            .stream()
            .filter { modelInfo: ModelInfo ->
                modelInfo.modelId == modelId
            }.findFirst()
            .orElse(null)

    @Throws(ContextOperationException::class)
    fun getModelByModelId(modelId: String): ModelInfo =
        searchModelByModelId(modelId)
            ?: throw ContextOperationException("Model was not found for modelId=$modelId.")

    @Throws(ContextOperationException::class)
    fun searchActiveContextByModelId(modelId: String): ConfigurableApplicationContext? {
        val containerInfo = searchActiveContainerInfoByModelId(modelId)
        if (containerInfo == null) {
            LOGGER.error("There is no active context found for model {}", modelId)
            return null
        }
        return containerInfo.context
    }

    @Throws(ContextOperationException::class)
    fun getActiveContextByModelId(modelId: String): ConfigurableApplicationContext =
        searchActiveContextByModelId(modelId)
            ?: throw ContextOperationException("No active context was found for modelId=$modelId. Probably there is no such model.")

    @Throws(ContextOperationException::class)
    private fun searchActiveContainerInfoByModelId(modelId: String): ContainerInfo? {
        val modelInfoList =
            modelsInfo
                .stream()
                .filter { modelInfo: ModelInfo ->
                    modelInfo.modelId == modelId
                }.toList()
        if (modelInfoList.size > 1) {
            LOGGER.error("Найдено больше одной модели  c ID {}", modelId)
            throw ContextOperationException("Найдено больше одной модели  c ID $modelId")
            //            return Optional.empty();
        } else if (modelInfoList.size == 1) {
            return modelInfoList[0]
                .containersInfo
                ?.stream()
                ?.filter(ContainerInfo::isActive)
                ?.findFirst()
                ?.get()
        }
        return null
    }

    fun upHttpRequestCount(modelId: String) {
        searchActiveContainerInfoByModelId(modelId)?.apply {
            this.requestStat.merge(REQUEST_NAME, 1L, java.lang.Long::sum)
        }
    }

    fun upHttpResponseCount(modelId: String) {
        searchActiveContainerInfoByModelId(modelId)?.apply {
            this.requestStat.merge(RESPONSE_NAME, 1L, java.lang.Long::sum)
        }
    }

    fun upHttpErrorCount(modelId: String) {
        searchActiveContainerInfoByModelId(modelId)?.apply {
            this.requestStat.merge(ERROR_NAME, 1L, java.lang.Long::sum)
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ModelMetaInfo::class.java)
    }
}
