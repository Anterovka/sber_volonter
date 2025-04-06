package ru.sbertech.dataspace.graphql.selection

import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.ELEMENTS_FIELD_NAME
import ru.sbertech.dataspace.primitive.Primitive
import ru.sbertech.dataspace.primitive.Text
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.universalvalue.UniversalValueVisitor
import ru.sbertech.dataspace.universalvalue.accept

object SelectionResultCreatingVisitor :
    UniversalValueVisitor<UniversalValue?> {
    override fun visit(
        object0: Map<String, UniversalValue?>,
        param: Unit,
    ): UniversalValue {
        val result = hashMapOf<String, UniversalValue?>()

        object0.forEach {
            result[it.key] = it.value?.accept(this)
        }

        return result
    }

    override fun visit(
        collection: Collection<UniversalValue?>,
        param: Unit,
    ): UniversalValue {
        val result = hashMapOf<String, UniversalValue>()
        val elems = arrayListOf<UniversalValue?>()

        collection.forEach {
            elems.add(it?.accept(this))
        }

        result[ELEMENTS_FIELD_NAME] = elems
        return result
    }

    override fun visit(
        type: PrimitiveType,
        value: Primitive,
        param: Unit,
    ): UniversalValue = value

    override fun visit(
        text: Text,
        param: Unit,
    ): UniversalValue = text.content
}
