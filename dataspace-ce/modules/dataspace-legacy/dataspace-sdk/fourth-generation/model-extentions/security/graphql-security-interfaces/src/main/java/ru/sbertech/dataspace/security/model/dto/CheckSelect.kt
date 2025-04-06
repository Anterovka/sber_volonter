package ru.sbertech.dataspace.security.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Objects

class CheckSelect : Comparable<CheckSelect> {
    @JsonProperty("objectId")
    var id: String? = null
    var typeName: String? = null
    var conditionValue: String? = null
    var description: String? = null
    var orderValue: Int = Int.MAX_VALUE / 2

    companion object {
        private val COMPARATOR =
            Comparator
                .comparing { obj: CheckSelect -> obj.id ?: "" }
                .thenComparing { obj: CheckSelect -> obj.orderValue }
                .thenComparing { obj: CheckSelect -> obj.typeName ?: "" }
                .thenComparing { obj: CheckSelect -> obj.conditionValue ?: "" }
                .thenComparing { obj: CheckSelect -> obj.description ?: "" }
    }

    override fun compareTo(other: CheckSelect): Int = COMPARATOR.compare(this, other)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as CheckSelect
        return orderValue == that.orderValue &&
            description == that.description &&
            typeName == that.typeName &&
            conditionValue == that.conditionValue
    }

    override fun hashCode(): Int = Objects.hash(description, typeName, conditionValue, orderValue)
}
