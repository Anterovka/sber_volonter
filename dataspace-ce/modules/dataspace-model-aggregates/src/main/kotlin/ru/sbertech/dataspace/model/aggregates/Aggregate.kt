package ru.sbertech.dataspace.model.aggregates

import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.type.EntityType
import java.util.WeakHashMap

class Aggregate {
    lateinit var type: EntityType
        private set

    lateinit var aggregateVersionPropertyName: String
        private set

    var idempotenceDataEntityType: EntityType? = null
        private set

    class Builder {
        var name: String? = null
        var aggregateVersionPropertyName: String? = null
        var leaves: MutableCollection<Leaf.Builder>? = null
        var externalReferences: MutableCollection<Pair<String, Boolean>>? = null
        var idempotenceDataEntityTypeName: String? = null

        fun build(
            model: Model,
            defaultIdempotenceDataEntityTypeName: String?,
            externalReferencesExtension: WeakHashMap<Any, Any>,
        ): Aggregate {
            val aggregate = Aggregate()

            aggregate.type = model.type(name!!) as EntityType
            aggregate.aggregateVersionPropertyName = aggregateVersionPropertyName!!

            val idempotenceDataEntityTypeName = idempotenceDataEntityTypeName ?: defaultIdempotenceDataEntityTypeName
            if (idempotenceDataEntityTypeName != null) {
                aggregate.idempotenceDataEntityType = model.type(idempotenceDataEntityTypeName) as EntityType
            }

            externalReferences?.forEach {
                val property = aggregate.type.property(it.first)
                externalReferencesExtension[property] =
                    if (it.second) ExternalReferenceOptionality.REQUIRED else ExternalReferenceOptionality.OPTIONAL
                if (property is EmbeddedProperty) {
                    externalReferencesExtension[property.type] = true
                }
            }

            return aggregate
        }
    }

    override fun toString(): String = "Aggregate(type=${type.name}, idempotenceDataEntityType=${idempotenceDataEntityType?.name})"
}
