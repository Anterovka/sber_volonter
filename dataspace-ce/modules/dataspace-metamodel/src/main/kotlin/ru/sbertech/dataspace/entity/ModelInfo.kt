package ru.sbertech.dataspace.entity

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ModelInfo(
    var modelId: String,
    var modelPath: String,
) {
    var containersInfo: MutableList<ContainerInfo>? = null

    fun addContainerInfo(containerInfo: ContainerInfo) {
        if (containersInfo == null) {
            containersInfo = ArrayList()
        }
        if (containersInfo!!.size > 2) {
            LOGGER.error("There cannot be more than 2 contexts for a model {}", modelId)
            return
        }
        containersInfo!!.add(containerInfo)
    }

    val activeContainerInfo: ContainerInfo?
        get() =
            containersInfo!!
                .stream()
                .filter(ContainerInfo::isActive)
                .findFirst()
                .get()

    val inActiveContainerInfo: ContainerInfo?
        get() =
            containersInfo!!
                .stream()
                .filter { containerInfo: ContainerInfo -> !containerInfo.isActive }
                .findFirst()
                .get()

    fun removeContainerInfo(containerInfo: ContainerInfo?): Boolean = containersInfo!!.remove(containerInfo)

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ModelInfo::class.java)
    }
}
