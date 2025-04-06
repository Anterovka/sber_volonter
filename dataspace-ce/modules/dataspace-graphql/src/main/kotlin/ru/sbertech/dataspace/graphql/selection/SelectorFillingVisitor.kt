package ru.sbertech.dataspace.graphql.selection

import graphql.language.Field
import ru.sbertech.dataspace.entitymanager.selector.Selector
import ru.sbertech.dataspace.expr.Expr
import ru.sbertech.dataspace.grammar.Grammar
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.EnumCollectionProperty
import ru.sbertech.dataspace.model.property.PrimitiveCollectionProperty
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.property.PropertyParameterizedVisitor

class SelectorFillingVisitor(
    private val grammar: Grammar<Expr>,
) : PropertyParameterizedVisitor<Field, Selector> {
    override fun visit(
        primitiveProperty: PrimitiveProperty,
        param: Field,
    ): Selector = Selector.PropertyBased(primitiveProperty.name)

    override fun visit(
        primitiveCollectionProperty: PrimitiveCollectionProperty,
        param: Field,
    ): Selector {
        val condition = param.getConditionExpr(grammar)
        return Selector.PropertyBased(primitiveCollectionProperty.name, cond = condition)
    }

    override fun visit(
        enumCollectionProperty: EnumCollectionProperty,
        param: Field,
    ): Selector {
        val condition = param.getConditionExpr(grammar)
        return Selector.PropertyBased(enumCollectionProperty.name, cond = condition)
    }

    override fun visit(
        embeddedProperty: EmbeddedProperty,
        param: Field,
    ): Selector {
        val selectorByName = linkedMapOf<String, Selector>()
        param.selectionSet
            .getSelectionsOfType(Field::class.java)
            .forEach {
                val property = embeddedProperty.type.property(it.name)
                selectorByName[it.alias ?: it.name] = property.accept(this, it)
            }

        return Selector.PropertyBased(embeddedProperty.name, selectorByName)
    }
}
