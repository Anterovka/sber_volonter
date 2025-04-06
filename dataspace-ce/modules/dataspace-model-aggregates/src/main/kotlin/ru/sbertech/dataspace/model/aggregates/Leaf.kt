package ru.sbertech.dataspace.model.aggregates

import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.Property
import ru.sbertech.dataspace.model.type.EntityType
import java.util.WeakHashMap

class Leaf {
    lateinit var aggregate: Aggregate
        private set

    lateinit var type: EntityType
        private set

    lateinit var parentProperty: Property
        private set

    lateinit var aggregateRootProperty: Property
        private set

    var treeParentProperty: Property? = null
        private set

    class Builder {
        var name: String? = null
        var parentProperty: String? = null
        var aggregateRootProperty: String? = null
        var externalReferences: MutableCollection<Pair<String, Boolean>>? = null
        var treeParentProperty: String? = null

        fun build(
            model: Model,
            aggregate: Aggregate,
            defaultAggregateRootPropertyName: String?,
            externalReferencesExtension: WeakHashMap<Any, Any>,
        ): Leaf {
            val leaf = Leaf()

            leaf.aggregate = aggregate
            val type = model.type(name!!) as EntityType
            leaf.type = type
            val aggregateRootProperty = type.inheritedPersistableProperty(aggregateRootProperty ?: defaultAggregateRootPropertyName!!)
            leaf.aggregateRootProperty = aggregateRootProperty
            leaf.parentProperty = type.inheritedPersistableProperty(parentProperty ?: aggregateRootProperty.name)
            leaf.treeParentProperty = treeParentProperty?.let { type.property(it) }

            externalReferences?.forEach {
                val property = leaf.type.property(it.first)
                externalReferencesExtension[property] =
                    if (it.second) ExternalReferenceOptionality.REQUIRED else ExternalReferenceOptionality.OPTIONAL
                if (property is EmbeddedProperty) {
                    externalReferencesExtension[property.type] = true
                }
            }

            return leaf
        }
    }

    override fun toString(): String =
        "Leaf(aggregate=${aggregate.type.name}, type=${type.name}, parentProperty=${parentProperty.name}, aggregateRootProperty=${aggregateRootProperty.name})"
}
