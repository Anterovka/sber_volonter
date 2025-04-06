package ru.sbertech.dataspace.modelcontext.configs

import graphql.execution.instrumentation.Instrumentation
import graphql.schema.GraphQLSchema
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.convert.ApplicationConversionService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.core.io.Resource
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.core.log.LogMessage
import org.springframework.format.FormatterRegistry
import org.springframework.graphql.ExecutionGraphQlService
import org.springframework.graphql.data.method.annotation.support.AnnotatedControllerConfigurer
import org.springframework.graphql.execution.BatchLoaderRegistry
import org.springframework.graphql.execution.DataFetcherExceptionResolver
import org.springframework.graphql.execution.DefaultBatchLoaderRegistry
import org.springframework.graphql.execution.DefaultExecutionGraphQlService
import org.springframework.graphql.execution.GraphQlSource
import org.springframework.graphql.execution.RuntimeWiringConfigurer
import org.springframework.graphql.execution.SubscriptionExceptionResolver
import org.springframework.graphql.server.WebGraphQlHandler
import org.springframework.graphql.server.WebGraphQlInterceptor
import org.springframework.graphql.server.webmvc.GraphQlHttpHandler
import org.springframework.graphql.server.webmvc.GraphQlSseHandler
import ru.sbertech.dataspace.graphql.exception.ExceptionResolver
import java.io.IOException
import java.util.Arrays
import java.util.concurrent.Executor

@EnableConfigurationProperties(
    GraphQlProperties::class,
)
@ImportRuntimeHints(CustomGraphQlAutoConfiguration.GraphQlResourcesRuntimeHints::class)
class CustomGraphQlAutoConfiguration(
    private val beanFactory: ListableBeanFactory,
) {
    @Bean
    @ConditionalOnMissingBean
    fun graphQlSource(
        resourcePatternResolver: ResourcePatternResolver,
        properties: GraphQlProperties,
        exceptionResolvers: ObjectProvider<DataFetcherExceptionResolver?>,
        subscriptionExceptionResolvers: ObjectProvider<SubscriptionExceptionResolver?>,
        instrumentations: ObjectProvider<Instrumentation?>,
        wiringConfigurers: ObjectProvider<RuntimeWiringConfigurer?>,
        sourceCustomizers: ObjectProvider<GraphQlSourceBuilderCustomizer>,
        @Value("\${child.model.path:mpath}") modelPath: String,
        graphQLSchema: GraphQLSchema,
    ): GraphQlSource {
//        val schemaLocations = arrayOf("file:$modelPath/")
//        val schemaResources =
//            resolveSchemaResources(
//                resourcePatternResolver,
//                schemaLocations,
//                properties.schema.fileExtensions,
//            )
//        val builder =
//            GraphQlSource
//                .schemaResourceBuilder()
//                .schemaResources(*schemaResources)
//                .exceptionResolvers(exceptionResolvers.orderedStream().toList())
//                .subscriptionExceptionResolvers(subscriptionExceptionResolvers.orderedStream().toList())
//                .instrumentation(instrumentations.orderedStream().toList())

        return GraphQlSource
            .builder(graphQLSchema)
            .exceptionResolvers(exceptionResolvers.orderedStream().toList())
            .instrumentation(instrumentations.orderedStream().toList())
            .build()
//        if (properties.schema.inspection.isEnabled) {
//            builder.inspectSchemaMappings { message: SchemaReport? ->
//                logger.info(
//                    message,
//                )
//            }
//        }
//        //        if (!properties.schema.introspection.isEnabled) {
//        //            Introspection.enabledJvmWide(false)
//        //        }
//        builder.configureTypeDefinitions(ConnectionTypeDefinitionConfigurer())
//        wiringConfigurers.orderedStream().forEach { configurer: RuntimeWiringConfigurer? ->
//            builder.configureRuntimeWiring(
//                configurer!!,
//            )
//        }
//        sourceCustomizers.orderedStream().forEach { customizer: GraphQlSourceBuilderCustomizer ->
//            customizer.customize(
//                builder,
//            )
//        }
//        return builder.build()
    }

    private fun resolveSchemaResources(
        resolver: ResourcePatternResolver,
        locations: Array<String>,
        extensions: Array<String>,
    ): Array<Resource> {
        val resources: MutableList<Resource> = ArrayList()
        for (location in locations) {
            for (extension in extensions) {
                resources.addAll(resolveSchemaResources(resolver, "$location*$extension"))
            }
        }
        return resources.toTypedArray<Resource>()
    }

    private fun resolveSchemaResources(
        resolver: ResourcePatternResolver,
        pattern: String,
    ): List<Resource> {
        try {
            return Arrays.asList(*resolver.getResources(pattern))
        } catch (ex: IOException) {
            logger.debug(LogMessage.format("Could not resolve schema location: '%s'", pattern), ex)
            return emptyList()
        }
    }

    @Bean
    @ConditionalOnMissingBean
    fun batchLoaderRegistry(): BatchLoaderRegistry = DefaultBatchLoaderRegistry()

    //    @ConditionalOnMissingBean
    @Bean
    fun executionGraphQlService(
        graphQlSource: GraphQlSource,
        batchLoaderRegistry: BatchLoaderRegistry,
    ): ExecutionGraphQlService {
        val service = DefaultExecutionGraphQlService(graphQlSource)
        service.addDataLoaderRegistrar(batchLoaderRegistry)
        return service
    }

    @Bean
    @ConditionalOnMissingBean
    fun annotatedControllerConfigurer(
        @Qualifier(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME) executorProvider: ObjectProvider<Executor?>,
    ): AnnotatedControllerConfigurer {
        val controllerConfigurer = AnnotatedControllerConfigurer()
        controllerConfigurer
            .addFormatterRegistrar { registry: FormatterRegistry? ->
                ApplicationConversionService.addBeans(
                    registry,
                    beanFactory,
                )
            }
        executorProvider.ifAvailable { executor: Executor? ->
            // почему-то переделка на установку свойства напрямую приводит к ошибке
            // Val cannot be reassigned
            @Suppress("UsePropertyAccessSyntax")
            controllerConfigurer.setExecutor(executor!!)
        }
        return controllerConfigurer
    }

    @Bean
    fun annotatedControllerConfigurerDataFetcherExceptionResolver(
        annotatedControllerConfigurer: AnnotatedControllerConfigurer,
    ): DataFetcherExceptionResolver = annotatedControllerConfigurer.exceptionResolver

    @Bean
    open fun exceptionResolver(): DataFetcherExceptionResolver = ExceptionResolver()

//    @ConditionalOnClass(ScrollPosition::class)
//    @Configuration(proxyBeanMethods = false)
//    internal class GraphQlDataAutoConfiguration {
//        @Bean
//        @ConditionalOnMissingBean
//        fun cursorStrategy(): EncodingCursorStrategy<ScrollPosition> =
//            CursorStrategy.withEncoder(ScrollPositionCursorStrategy(), CursorEncoder.base64())
//
//        @Bean
//        fun cursorStrategyCustomizer(cursorStrategy: CursorStrategy<*>): GraphQlSourceBuilderCustomizer {
//            if (cursorStrategy.supports(ScrollPosition::class.java)) {
//                val scrollCursorStrategy = cursorStrategy as CursorStrategy<ScrollPosition>
//                val connectionFieldTypeVisitor =
//                    ConnectionFieldTypeVisitor
//                        .create(
//                            java.util.List.of<ConnectionAdapter>(
//                                WindowConnectionAdapter(scrollCursorStrategy),
//                                SliceConnectionAdapter(scrollCursorStrategy),
//                            ),
//                        )
//                return GraphQlSourceBuilderCustomizer { builder: SchemaResourceBuilder ->
//                    builder.typeVisitors(
//                        java.util.List.of<GraphQLTypeVisitor>(
//                            connectionFieldTypeVisitor,
//                        ),
//                    )
//                }
//            }
//            return GraphQlSourceBuilderCustomizer { builder: SchemaResourceBuilder? -> }
//        }
//    }

    internal class GraphQlResourcesRuntimeHints : RuntimeHintsRegistrar {
        override fun registerHints(
            hints: RuntimeHints,
            classLoader: ClassLoader?,
        ) {
            hints.resources().registerPattern("graphql/*.graphqls").registerPattern("graphql/*.gqls")
        }
    }

    @Bean
    @ConditionalOnMissingBean
    fun graphQlSseHandler(webGraphQlHandler: WebGraphQlHandler): GraphQlSseHandler = GraphQlSseHandler(webGraphQlHandler)

    @Bean
    @ConditionalOnMissingBean
    fun webGraphQlHandler(
        service: ExecutionGraphQlService,
        interceptors: ObjectProvider<WebGraphQlInterceptor?>,
    ): WebGraphQlHandler = WebGraphQlHandler.builder(service).interceptors(interceptors.orderedStream().toList()).build()

    //    @ConditionalOnMissingBean
    @Bean()
    fun childGraphQlHttpHandler(webGraphQlHandler: WebGraphQlHandler): GraphQlHttpHandler = GraphQlHttpHandler(webGraphQlHandler)

    companion object {
        private val logger: Log = LogFactory.getLog(CustomGraphQlAutoConfiguration::class.java)
    }
}
