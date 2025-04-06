package ru.sbertech.dataspace.model.system.extension

import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.type.EntityType
import java.util.StringJoiner

data class IndexProperty(
    val name: String,
    val parentPropertyName: String? = null,
)

class Index {
    var isUnique: Boolean = false
    lateinit var name: String
        private set
    lateinit var properties: Collection<IndexProperty>
        private set

    class Builder {
        var isUnique: Boolean = false
        var name: String? = null
        var properties: MutableCollection<String>? = null

        fun build(entityType: EntityType): Index {
            val index = Index()
            index.isUnique = isUnique

            val indexName = StringJoiner("_")
            index.properties = properties?.map { propertyName ->
                if (propertyName.contains(".")) {
                    val embeddedPropertyPath = propertyName.split(".")
                    val embeddedProperty = embeddedPropertyPath[0].let { entityType.property(it) } as EmbeddedProperty
                    indexName.add(propertyName.replace(".", "__"))
                    val property = embeddedProperty.embeddedType.property(embeddedPropertyPath[1])
                    IndexProperty(property.name, embeddedProperty.name)
                } else {
                    indexName.add(propertyName)
                    entityType.property(propertyName)
                    IndexProperty(propertyName)
                }
            } ?: throw IllegalStateException("The index '${index.name}' must contain at least one property")

            index.name = name ?: indexName.toString()

            return index
        }
    }
}
