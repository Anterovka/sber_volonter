package ru.sbertech.dataspace.model.aggregates

import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.property.Property
import ru.sbertech.dataspace.model.type.Type
import java.util.WeakHashMap

private val aggregatesModelExtension = WeakHashMap<Any, Any>()

internal enum class ExternalReferenceOptionality {
    REQUIRED,
    OPTIONAL,
}

val Model.aggregatesModel: AggregatesModel? get() = aggregatesModelExtension[this] as AggregatesModel?
val Property.isExternalReference get() = aggregatesModelExtension.contains(this)
val Property.isMandatoryExternalReference get() =
    aggregatesModelExtension[this] as? ExternalReferenceOptionality? == ExternalReferenceOptionality.REQUIRED
val Type.isExternalReference get() = aggregatesModelExtension[this] as Boolean? ?: false

class AggregatesModel {
    private lateinit var aggregateOrLeafByTypeName: Map<String, Any>

    fun aggregateOrLeaf(typeName: String): Any =
        aggregateOrLeafByTypeName[typeName] ?: throw IllegalArgumentException("Aggregate or Leaf for '$typeName' is not found")

    class Builder {
        var aggregates: MutableCollection<Aggregate.Builder>? = null

        var externalReferenceTypes: MutableCollection<String>? = null

        var defaultIdempotenceDataEntityTypeName: String? = null

        var defaultAggregateRootPropertyName: String? = null

        fun build(model: Model): AggregatesModel {
            val aggregatesModel = AggregatesModel()

            val aggregateOrLeafByTypeName = hashMapOf<String, Any>()

            aggregates?.forEach { aggregateBuilder ->
                val aggregate = aggregateBuilder.build(model, defaultIdempotenceDataEntityTypeName, aggregatesModelExtension)
                aggregateOrLeafByTypeName[aggregateBuilder.name!!] = aggregate

                aggregateBuilder.leaves?.forEach { leafBuilder ->
                    aggregateOrLeafByTypeName[leafBuilder.name!!] =
                        leafBuilder.build(model, aggregate, defaultAggregateRootPropertyName, aggregatesModelExtension)
                }
            }

            aggregatesModel.aggregateOrLeafByTypeName = aggregateOrLeafByTypeName

            externalReferenceTypes?.forEach {
                aggregatesModelExtension[model.type(it)] = true
            }

            aggregatesModelExtension[model] = aggregatesModel
            return aggregatesModel
        }
    }
}
