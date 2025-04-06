package ru.sbertech.dataspace.configs.metrics

import io.micrometer.common.KeyValue
import io.micrometer.common.KeyValues
import org.springframework.http.server.observation.DefaultServerRequestObservationConvention
import org.springframework.http.server.observation.ServerRequestObservationContext

class DataspaceTagCustomizer : DefaultServerRequestObservationConvention() {
    override fun getLowCardinalityKeyValues(context: ServerRequestObservationContext): KeyValues {
        val modelId = searchModelIdInUri(context)
        val customTag: KeyValue = KeyValue.of("modelId", modelId)
        return KeyValues.of(exception(context), method(context), outcome(context), status(context), uri(context), customTag)
    }

    private fun searchModelIdInUri(context: ServerRequestObservationContext): String {
        val uri = context.carrier.requestURI
        val modelId = uri.substring(uri.lastIndexOf("/") + 1)
        return modelId
    }
}
