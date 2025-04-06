package ru.sbertech.dataspace.graphql.command

import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.ADD_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.CLEAR_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.REMOVE_FIELD_NAME
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.EnumCollectionProperty
import ru.sbertech.dataspace.model.property.EnumProperty
import ru.sbertech.dataspace.model.property.MappedReferenceCollectionProperty
import ru.sbertech.dataspace.model.property.PrimitiveCollectionProperty
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.property.PropertyParameterizedVisitor
import ru.sbertech.dataspace.model.property.ReferenceProperty
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.uow.command.ExternalReferencesCollection
import ru.sbertech.dataspace.uow.packet.CommandRefContext

class PropertyValuesFillingVisitor(
    commandQualifier: String,
    commandRefContext: CommandRefContext,
) : GraphQLValueToPrimitiveConvertingVisitor(commandQualifier, commandRefContext),
    PropertyParameterizedVisitor<Any?, UniversalValue?> {
    override fun visit(
        primitiveProperty: PrimitiveProperty,
        param: Any?,
    ): UniversalValue? = primitiveProperty.type.accept(this, param)

    override fun visit(
        primitiveCollectionProperty: PrimitiveCollectionProperty,
        param: Any?,
    ): UniversalValue =
        if (param == null) {
            emptyList()
        } else {
            (param as List<*>).map {
                primitiveCollectionProperty.type.accept(this, it)
            }
        }

    override fun visit(
        enumCollectionProperty: EnumCollectionProperty,
        param: Any?,
    ): UniversalValue =
        if (param == null) {
            emptyList()
        } else {
            (param as List<*>).map {
                PrimitiveType.String.accept(this, it)
            }
        }

    override fun visit(
        embeddedProperty: EmbeddedProperty,
        param: Any?,
    ): UniversalValue {
        val embeddedPropertyValue = linkedMapOf<String, UniversalValue?>()
        (param as Map<String, Any?>?)?.forEach {
            val property = embeddedProperty.type.property(it.key)
            embeddedPropertyValue[property.name] = property.accept(this, it.value)
        }

        return embeddedPropertyValue
    }

    override fun visit(
        referenceProperty: ReferenceProperty,
        param: Any?,
    ): UniversalValue? = referenceProperty.type.tableIdProperty.accept(this, param)

    override fun visit(
        enumProperty: EnumProperty,
        param: Any?,
    ): UniversalValue? = param

    override fun visit(
        mappedReferenceCollectionProperty: MappedReferenceCollectionProperty,
        param: Any?,
    ): UniversalValue {
        val input = param as Map<String, Any>

        val clear = input[CLEAR_FIELD_NAME] as Boolean
        val add = arrayListOf<LinkedHashMap<String, UniversalValue?>>()
        val remove = arrayListOf<LinkedHashMap<String, UniversalValue?>>()

        (input[ADD_FIELD_NAME] as List<*>)
            .asSequence()
            .forEach {
                add.add(linkedMapOf("reference" to it))
            }

        (input[REMOVE_FIELD_NAME] as List<*>)
            .asSequence()
            .forEach {
                remove.add(linkedMapOf("reference" to it))
            }

        return ExternalReferencesCollection(
            mappedReferenceCollectionProperty.type,
            clear,
            add,
            remove,
        )
    }
}
