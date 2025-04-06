package ru.sbertech.dataspace.uow.command

import java.math.BigDecimal

class Increment(
    val fieldName: String,
    private val delta: Number,
    private val isNegative: Boolean,
    private val incrementCheck: IncrementCheck?,
) {
    fun execute(oldValue: Number?): Number {
        val newValue = calculate(oldValue)
        incrementCheck?.check(newValue, fieldName)
        return newValue
    }

    private fun calculate(oldValue: Number?): Number =
        when (delta) {
            is Int -> {
                handleInt(oldValue, delta)
            }

            is Long -> {
                handleLong(oldValue, delta)
            }

            is Double -> {
                handleDouble(oldValue, delta)
            }

            is Float -> {
                handleFloat(oldValue, delta)
            }

            is BigDecimal -> {
                handleBigDecimal(oldValue, delta)
            }

            else -> throw IllegalArgumentException("Unknown numeric type")
        }

    private fun handleInt(
        oldValue: Number?,
        delta: Int,
    ): Number {
        val oldIntValue = (oldValue ?: 0).toInt()
        return if (!isNegative) {
            oldIntValue + delta
        } else {
            oldIntValue - delta
        }
    }

    private fun handleLong(
        oldValue: Number?,
        delta: Long,
    ): Number {
        val oldLongValue = (oldValue ?: 0L).toLong()
        return if (!isNegative) {
            oldLongValue + delta
        } else {
            oldLongValue - delta
        }
    }

    private fun handleDouble(
        oldValue: Number?,
        delta: Double,
    ): Number {
        val oldDoubleValue = (oldValue ?: 0.0).toDouble()
        return if (!isNegative) {
            oldDoubleValue + delta
        } else {
            oldDoubleValue - delta
        }
    }

    private fun handleFloat(
        oldValue: Number?,
        delta: Float,
    ): Number {
        val oldFloatValue = (oldValue ?: 0F).toFloat()
        return if (!isNegative) {
            oldFloatValue + delta
        } else {
            oldFloatValue - delta
        }
    }

    private fun handleBigDecimal(
        oldValue: Number?,
        delta: BigDecimal,
    ): Number {
        val oldBigDecimalValue = (oldValue as BigDecimal? ?: BigDecimal.ZERO)
        return if (!isNegative) {
            oldBigDecimalValue.add(delta)
        } else {
            oldBigDecimalValue.subtract(delta)
        }
    }

    override fun toString(): String =
        "Increment(fieldName='$fieldName', delta=$delta, isNegative=$isNegative, incrementCheck=$incrementCheck)"
}
