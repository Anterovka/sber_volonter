package ru.sbertech.dataspace.grammar.expr

import ru.sbertech.dataspace.expr.Expr
import ru.sbertech.dataspace.grammar.Grammar
import ru.sbertech.dataspace.grammar.expr.parse.ExprParser
import ru.sbertech.dataspace.grammar.expr.tostring.ToStringConvertingVisitor

class ExprGrammar : Grammar<Expr> {
    override fun appendTo(
        appendable: Appendable,
        value: Expr,
    ) {
        when (appendable) {
            is StringBuilder -> value.accept(ToStringConvertingVisitor(appendable))
            else -> appendable.append(StringBuilder().also { value.accept(ToStringConvertingVisitor(it)) })
        }
    }

    override fun parse(charSequence: CharSequence) = ExprParser(charSequence).parse()
}
