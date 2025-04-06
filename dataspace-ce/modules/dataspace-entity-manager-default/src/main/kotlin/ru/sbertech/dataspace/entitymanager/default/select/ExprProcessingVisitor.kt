package ru.sbertech.dataspace.entitymanager.default.select

import ru.sbertech.dataspace.entitymanager.default.select.exprselector.CondSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.ExprSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.PrimitiveSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.embedded.EmbeddedSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.entity.EntitySelector
import ru.sbertech.dataspace.expr.Expr
import ru.sbertech.dataspace.expr.ExprVisitor
import ru.sbertech.dataspace.sql.expr.Expr as SqlExpr

internal class ExprProcessingVisitor(
    private val context: SelectContext,
    private val cur: ExprSelector?,
    private val root: ExprSelector?,
) : ExprVisitor<ExprSelector> {
    override fun visit(
        value: Expr.Value,
        param: Unit,
    ) = context.addParam(value.value).let { PrimitiveSelector(it.type, it) }

    override fun visit(
        cur: Expr.Cur,
        param: Unit,
    ) = this.cur ?: throw IllegalArgumentException("TODO")

    override fun visit(
        root: Expr.Root,
        param: Unit,
    ) = this.root ?: throw IllegalArgumentException("TODO")

    override fun visit(
        property: Expr.Property,
        param: Unit,
    ) = when (val expr = property.expr.accept(this)) {
        is EntitySelector -> expr.type.inheritedPersistableProperty(property.name).accept(PropertyProcessingVisitor.ForEntity, expr)
        is EmbeddedSelector -> expr.type.property(property.name).accept(PropertyProcessingVisitor.ForEmbedded, expr)
        else -> throw IllegalArgumentException()
    }

    override fun visit(
        eq: Expr.Eq,
        param: Unit,
    ) = eq.expr1.accept(this).eq(eq.expr2.accept(this))

    override fun visit(
        and: Expr.And,
        param: Unit,
    ): ExprSelector {
        val expr1 = and.expr1.accept(this)
        val expr2 = and.expr2.accept(this)
        return when {
            expr1 is CondSelector && expr2 is CondSelector -> CondSelector(SqlExpr.And(expr1.sqlExpr, expr2.sqlExpr))
            else -> throw IllegalArgumentException()
        }
    }

    override fun visit(
        or: Expr.Or,
        param: Unit,
    ): ExprSelector {
        val expr1 = or.expr1.accept(this)
        val expr2 = or.expr2.accept(this)
        return when {
            expr1 is CondSelector && expr2 is CondSelector -> CondSelector(SqlExpr.Or(expr1.sqlExpr, expr2.sqlExpr))
            else -> throw IllegalArgumentException()
        }
    }
}
