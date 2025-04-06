package ru.sbertech.dataspace.configs

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.EncodedResourceResolver

@ConditionalOnProperty(name = ["graphql.graphiql.cdn.enabled"], havingValue = "false", matchIfMissing = false)
class GraphiQLResourcesConfig : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry
            .addResourceHandler("/**")
            .addResourceLocations(*CLASSPATH_RESOURCE_LOCATIONS)
            .resourceChain(true)
            .addResolver(EncodedResourceResolver())
    }

    companion object {
        private val CLASSPATH_RESOURCE_LOCATIONS =
            arrayOf(
                "classpath:/static/",
                "classpath:/static/js/",
                "classpath:/static/css/",
            )
    }
}
