package ru.sbertech.dataspace.model

import ru.sbertech.dataspace.model.type.Type

class Model private constructor() : Component() {
    val types: Collection<Type> get() = typeByName.values

    private lateinit var typeByName: Map<String, Type>

    fun type(name: String): Type = typeByName[name] ?: throw IllegalArgumentException("Type '$name' is not found")

    class Builder : Component.Builder() {
        var types: MutableCollection<Type.Builder>? = null

        override val internal = Internal()

        fun validate(): Collection<ModelError> = internal.validate()

        fun build(): Model = internal.build()

        override fun clone() = Builder().also { internal.setCloneProperties(it) }

        internal inner class Internal : Component.Builder.Internal() {
            private lateinit var attributePathByTypeNameForValidate: MutableMap<String, () -> String>

            private lateinit var metaByTableForValidate: MutableMap<String, TableMeta>

            override lateinit var result: Model
                private set

            override val meta get() = Meta

            override val about get() = "Model '$name'"

            fun type(name: String): Type.Builder? = types?.find { it.name == name }

            fun validateUniqueTypeName(
                errors: MutableCollection<ModelError>,
                typeName: String,
                typeNameAttributePath: () -> String,
            ) {
                validateUniqueValue(errors, typeName, typeNameAttributePath, attributePathByTypeNameForValidate)
            }

            fun validateUniqueTable(
                errors: MutableCollection<ModelError>,
                table: String,
                tableAttributePath: () -> String,
            ) {
                val meta = metaByTableForValidate.getOrPut(table) { TableMeta() }
                validateUniqueValue(errors, table, tableAttributePath, { meta.tableAttributePath }, { meta.tableAttributePath = it })
            }

            fun validateUniqueColumn(
                errors: MutableCollection<ModelError>,
                table: String,
                column: String,
                columnAttributePath: () -> String,
            ) {
                val attributePathByColumn = metaByTableForValidate.getOrPut(table) { TableMeta() }.attributePathByColumn
                validateUniqueValue(errors, column, columnAttributePath, attributePathByColumn)
            }

            fun validate(): Collection<ModelError> {
                prepare(Goal.VALIDATE, this@Builder, null, null)
                return arrayListOf<ModelError>().also { validate(it) }
            }

            fun build(): Model {
                prepare(Goal.BUILD, this@Builder, null, null)
                createResult()
                setResultProperties()
                return result
            }

            override fun setCloneProperties(clone: AbstractBuilder) {
                super.setCloneProperties(clone as Builder)
                clone.types = types?.mapTo(arrayListOf()) { it.clone() }
            }

            override fun prepare(
                goal: Goal,
                model: Builder,
                parent: AbstractBuilder?,
                parentRelation: Relation<*>?,
            ) {
                super.prepare(goal, model, parent, parentRelation)
                when (goal) {
                    Goal.VALIDATE -> {
                        attributePathByTypeNameForValidate = linkedMapOf()
                        metaByTableForValidate = linkedMapOf()
                    }

                    Goal.BUILD -> {}
                }
            }

            override fun createResult() {
                super.createResult()
                result = Model()
            }

            override fun setResultProperties() {
                super.setResultProperties()
                result.typeByName =
                    types
                        ?.asSequence()
                        .orEmpty()
                        .map { it.internal.result }
                        .associateByTo(linkedMapOf()) { it.name }
            }
        }

        internal object Meta : Component.Builder.Meta<Builder>() {
            val types: Relation<Builder> = buildersRelation(TYPES_ATTRIBUTE) { it.types }
        }

        companion object {
            internal const val TYPES_ATTRIBUTE: String = "types"
        }
    }
}
