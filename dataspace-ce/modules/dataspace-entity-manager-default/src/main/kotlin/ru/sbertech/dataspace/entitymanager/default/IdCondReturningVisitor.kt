package ru.sbertech.dataspace.entitymanager.default

import ru.sbertech.dataspace.model.property.BasicProperty
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.EnumProperty
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.property.PropertyParameterizedVisitor
import ru.sbertech.dataspace.model.property.ReferenceProperty
import ru.sbertech.dataspace.sql.expr.Expr as SqlExpr

internal object IdCondReturningVisitor : PropertyParameterizedVisitor<MutableMap<String, SqlExpr.Param>, SqlExpr> {
    private fun visitBasicProperty(
        basicProperty: BasicProperty,
        paramByColumn: MutableMap<String, SqlExpr.Param>,
    ) = SqlExpr.Eq(SqlExpr.Column(basicProperty.column), paramByColumn.getValue(basicProperty.column))

    override fun visit(
        primitiveProperty: PrimitiveProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") paramByColumn: MutableMap<String, SqlExpr.Param>,
    ) = visitBasicProperty(primitiveProperty, paramByColumn)

    override fun visit(
        enumProperty: EnumProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") paramByColumn: MutableMap<String, SqlExpr.Param>,
    ) = visitBasicProperty(enumProperty, paramByColumn)

    override fun visit(
        embeddedProperty: EmbeddedProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") paramByColumn: MutableMap<String, SqlExpr.Param>,
    ) = embeddedProperty.embeddedType.properties
        .asSequence()
        .map { it.accept(this, paramByColumn) }
        .reduce { expr1, expr2 -> SqlExpr.And(expr1, expr2) }

    override fun visit(
        referenceProperty: ReferenceProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") paramByColumn: MutableMap<String, SqlExpr.Param>,
    ) = referenceProperty.idProperty.accept(this, paramByColumn)
}
