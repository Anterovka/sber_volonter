package ru.sbertech.dataspace.entitymanager.default.select.exprselector

import ru.sbertech.dataspace.entitymanager.default.select.subquerybuilder.SimpleSubQueryBuilder
import ru.sbertech.dataspace.entitymanager.default.select.valuereader.ValueReader

internal abstract class ExprSelector {
    inline fun <reified T : ExprSelector> cast(): T = this as? T ?: throw IllegalArgumentException("TODO")

    open fun reader(subQuery: SimpleSubQueryBuilder): ValueReader = throw IllegalStateException("TODO")

    // TODO Вынести в отдельный интерфейс со всеми операциями с возможностью переиспользовать для алгоритма определения типа выражения?
    open fun eq(expr: ExprSelector): CondSelector = throw IllegalArgumentException("TODO")
}
