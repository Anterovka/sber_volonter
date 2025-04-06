package ru.sbertech.dataspace.model.system.extension

import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.system.typeExtension
import ru.sbertech.dataspace.model.type.EntityType

class EntityTypeExtension {
    lateinit var type: EntityType
        private set
    var isSystem: Boolean = false
        private set
    private lateinit var indexByName: Lazy<Map<String, Index>>

    val indexes: Collection<Index> get() = indexByName.value.values

    fun getIndex(indexName: String): Index =
        checkNotNull(indexByName.value[indexName]) { "The index with name '$indexName' is not found for entityType '${type.name}'" }

    class Builder {
        var typeName: String? = null
        var isSystem: Boolean = false
        var indexes: MutableCollection<Index.Builder>? = null

        fun build(model: Model): EntityTypeExtension {
            val entityTypeExtension = EntityTypeExtension()
            entityTypeExtension.type =
                model.type(requireNotNull(typeName) { "typeName for EntityTypeExtension cannot be null" }) as EntityType
            entityTypeExtension.isSystem = isSystem

            entityTypeExtension.indexByName =
                lazy(LazyThreadSafetyMode.NONE) {
                    val indexByName =
                        indexes?.associateTo(mutableMapOf()) {
                            val index = it.build(entityTypeExtension.type)
                            index.name to index
                        } ?: mutableMapOf()
                    indexByName.putAll(getIndexesForParentTypes(entityTypeExtension.type))
                    return@lazy indexByName
                }

            return entityTypeExtension
        }

        private fun getIndexesForParentTypes(entityType: EntityType): Map<String, Index> {
            val indexByNameFromParents = mutableMapOf<String, Index>()
            var currentType: EntityType? = entityType.parentEntityType

            while (currentType != null) {
                val parentIndexes = currentType.typeExtension.indexByName

                indexByNameFromParents.putAll(parentIndexes.value)
                currentType = currentType.parentEntityType
            }

            return indexByNameFromParents
        }
    }
}
