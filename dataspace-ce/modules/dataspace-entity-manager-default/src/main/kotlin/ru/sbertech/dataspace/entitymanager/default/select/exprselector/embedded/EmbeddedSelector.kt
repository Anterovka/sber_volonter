package ru.sbertech.dataspace.entitymanager.default.select.exprselector.embedded

import ru.sbertech.dataspace.entitymanager.default.select.PropertyProcessingVisitor
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.CondSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.ExprSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.PrimitiveSelector
import ru.sbertech.dataspace.entitymanager.default.select.subquerybuilder.SimpleSubQueryBuilder
import ru.sbertech.dataspace.entitymanager.default.select.valuereader.ObjectReader
import ru.sbertech.dataspace.model.EmbeddedType
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.EnumProperty
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.sql.expr.Expr

internal abstract class EmbeddedSelector : ExprSelector() {
    abstract val type: EmbeddedType

    abstract val id: ExprSelector?

    abstract fun property(primitiveProperty: PrimitiveProperty): PrimitiveSelector

    abstract fun property(enumProperty: EnumProperty): PrimitiveSelector

    abstract fun property(embeddedProperty: EmbeddedProperty): EmbeddedSelector

    override fun reader(subQuery: SimpleSubQueryBuilder) =
        ObjectReader(
            type.properties.associateByTo(
                linkedMapOf(),
                { it.name },
                { it.accept(PropertyProcessingVisitor.ForEmbedded, this).reader(subQuery) },
            ),
        )

    override fun eq(expr: ExprSelector) =
        when {
            expr is EmbeddedSelector && type.embeddableType == expr.type.embeddableType ->
                type.properties
                    .asSequence()
                    .map {
                        it
                            .accept(PropertyProcessingVisitor.ForEmbedded, this)
                            .eq(expr.type.property(it.name).accept(PropertyProcessingVisitor.ForEmbedded, expr))
                    }.reduce { acc, condSelector -> CondSelector(Expr.And(acc.sqlExpr, condSelector.sqlExpr)) }

            else -> throw IllegalArgumentException()
        }
}
