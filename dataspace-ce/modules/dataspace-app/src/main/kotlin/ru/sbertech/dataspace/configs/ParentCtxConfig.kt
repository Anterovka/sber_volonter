package ru.sbertech.dataspace.configs

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment
import ru.sbertech.dataspace.configs.metrics.DataspaceTagCustomizer
import ru.sbertech.dataspace.entity.ModelMetaInfo
import ru.sbertech.dataspace.properties.AppProperties
import ru.sbertech.dataspace.security.config.SecurityConfig
import ru.sbertech.dataspace.services.ContextManager
import ru.sbertech.dataspace.services.FileListeners
import ru.sbertech.dataspace.services.FileListenersHolder
import ru.sbertech.dataspace.services.FileWatchListener
import ru.sbertech.dataspace.services.K8sConfigMapContextManager
import ru.sbertech.dataspace.services.K8sConfigMapListener
import ru.sbertech.dataspace.services.LinuxContextManager
import ru.sbertech.dataspace.services.LinuxFileListener
import ru.sbertech.dataspace.services.PathWatchService
import ru.sbertech.dataspace.util.ModelResolver

@SpringBootApplication(
    exclude = [
        DataSourceAutoConfiguration::class,
    ],
)
@ComponentScan("ru.sbertech.dataspace.mvc")
@Import(
    GraphiQlConfig::class,
    SecurityConfig::class,
)
@EnableConfigurationProperties(AppProperties::class)
class ParentCtxConfig {
    @Bean
    fun fileListenersHolder(): FileListenersHolder = FileListeners()

    @Bean
    fun pathWatchService(
        appProperties: AppProperties,
        fileListenersHolder: FileListenersHolder,
    ): PathWatchService = PathWatchService(appProperties, fileListenersHolder)

    @Bean
    fun modelMetaInfo(properties: AppProperties): ModelMetaInfo = ModelMetaInfo()

    @Bean
    fun modelResolver(
        modelMetaInfo: ModelMetaInfo,
        properties: AppProperties,
    ): ModelResolver = ModelResolver(properties.singleMode, properties.defaultModelId, modelMetaInfo)

    @Bean
    fun contextManager(
        parentContext: ApplicationContext,
        modelMetaInfo: ModelMetaInfo,
        environment: Environment,
        properties: AppProperties,
    ): ContextManager =
        if (properties.filesystemK8sEnabled
        ) {
            K8sConfigMapContextManager(parentContext, modelMetaInfo, environment, properties)
        } else {
            LinuxContextManager(parentContext, modelMetaInfo, environment, properties)
        }

    @Bean
    fun fileWatchListener(
        listenersHolder: FileListenersHolder,
        modelMetaInfo: ModelMetaInfo,
        contextManager: ContextManager,
        properties: AppProperties,
    ): FileWatchListener =
        if (properties.filesystemK8sEnabled
        ) {
            K8sConfigMapListener(listenersHolder, modelMetaInfo, contextManager, properties)
        } else {
            LinuxFileListener(listenersHolder, contextManager)
        }

    @Bean
    fun customDefaultServerRequestObsConvention(): DataspaceTagCustomizer = DataspaceTagCustomizer()

    companion object {
        private val logger: Log = LogFactory.getLog(ParentCtxConfig::class.java)
    }
}
