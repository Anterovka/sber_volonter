package ru.sbertech.dataspace.security.config

// TODO кажется здесь больше подходит data-класс
class StatusInfo private constructor(
    val stakeholderCode: String?,
    val statusCode: String,
) {
    companion object {
        @JvmStatic
        fun of(
            stakeholderCode: String?,
            statusCode: String,
        ): StatusInfo = StatusInfo(stakeholderCode, statusCode)

        @JvmStatic
        fun of(statusCode: String): StatusInfo = StatusInfo(null, statusCode)
    }

    fun hasStakeholder(): Boolean = stakeholderCode != null && !stakeholderCode.isEmpty()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StatusInfo

        if (stakeholderCode != other.stakeholderCode) return false
        if (statusCode != other.statusCode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = stakeholderCode?.hashCode() ?: 0
        result = 31 * result + statusCode.hashCode()
        return result
    }
}
