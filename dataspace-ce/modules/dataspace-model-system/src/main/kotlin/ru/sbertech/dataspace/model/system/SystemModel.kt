package ru.sbertech.dataspace.model.system

import ru.sbertech.dataspace.common.uncheckedCast
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.property.Property
import ru.sbertech.dataspace.model.system.extension.EntityTypeExtension
import ru.sbertech.dataspace.model.system.extension.Index
import ru.sbertech.dataspace.model.type.EmbeddableType
import ru.sbertech.dataspace.model.type.EntityType
import java.util.WeakHashMap

private val systemModelExtension = WeakHashMap<Any, Any>()

val EntityType.typeExtension get(): EntityTypeExtension = systemModelExtension[this] as EntityTypeExtension
val EntityType.isSystem get(): Boolean = (systemModelExtension[this] as EntityTypeExtension?)?.isSystem ?: false
val EntityType.indexes get(): Collection<Index> = (systemModelExtension[this] as EntityTypeExtension?)?.indexes ?: emptyList()
val Property.isSystem get() = systemModelExtension.containsKey(this)
val EmbeddableType.requiredProperties get() = systemModelExtension[this]?.uncheckedCast<List<Property>>() ?: emptyList()

fun EntityType.getIndex(indexName: String): Index {
    val entityTypeExtension = systemModelExtension[this] as EntityTypeExtension?
    if (entityTypeExtension == null || entityTypeExtension.indexes.isEmpty()) {
        throw IllegalStateException("The entity '${this.name}' doesnt have any indexes")
    }

    return entityTypeExtension.getIndex(indexName)
}

class SystemModel {
    class Builder {
        var entityTypeExtensions: MutableCollection<EntityTypeExtension.Builder>? = null
        var systemPropertiesForEntities: MutableCollection<SystemPropertiesForEntity.Builder>? = null
        var embeddableWithRequiredProperties = mutableMapOf<String, Set<String>>()

        fun build(model: Model): SystemModel {
            val systemModel = SystemModel()

            entityTypeExtensions?.forEach {
                val entityTypeExtension = it.build(model)
                systemModelExtension[entityTypeExtension.type] = entityTypeExtension
            }

            systemPropertiesForEntities?.forEach {
                val systemPropertiesForEntity = it.build(model)
                systemPropertiesForEntity.systemProperties.forEach { property ->
                    systemModelExtension[property] = true
                }
            }

            embeddableWithRequiredProperties.forEach { (name, requiredProperties) ->
                val embeddable = model.type(name) as EmbeddableType
                systemModelExtension[embeddable] =
                    requiredProperties
                        .map { requiredPropertyName -> embeddable.property(requiredPropertyName) }
                        .toList()
            }

            return systemModel
        }
    }
}
