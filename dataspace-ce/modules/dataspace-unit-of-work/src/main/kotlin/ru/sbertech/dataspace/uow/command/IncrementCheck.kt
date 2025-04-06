package ru.sbertech.dataspace.uow.command

import java.util.function.IntPredicate

class IncrementCheck(
    val value: Number,
    private val operator: Operator,
) {
    enum class Operator(
        val code: String,
        private val predicate: IntPredicate,
    ) {
        LESS("lt", { v -> v < 0 }),
        LESS_OR_EQUAL("le", { v -> (v < 0) || (v == 0) }),
        GREATER("gt", { v -> v > 0 }),
        GREATER_OR_EQUAL("ge", { v -> (v > 0) || (v == 0) }),
        ;

        fun test(
            left: Comparable<Any?>,
            right: Comparable<Any?>?,
        ): Boolean = predicate.test(left.compareTo(right))

        override fun toString(): String = "Operator(code='$code')"

        companion object {
            fun getByCode(code: String): Operator = Operator.entries.first { it.code == code }
        }
    }

    fun check(
        newValue: Number,
        fieldName: String,
    ) {
        if (!operator.test(newValue as Comparable<Any?>, value as Comparable<Any?>)) {
            throw IllegalStateException(
                "Increment error: the new value '$newValue' for the field '$fieldName' does not match the condition '${operator.name} $value'",
            )
        }
    }

    override fun toString(): String = "IncrementCheck(value=$value, operator=$operator)"
}
