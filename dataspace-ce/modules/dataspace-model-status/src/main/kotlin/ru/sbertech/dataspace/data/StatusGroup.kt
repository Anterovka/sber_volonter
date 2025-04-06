package ru.sbertech.dataspace.data

class StatusGroup {
    lateinit var code: String
        private set

    override fun toString(): String = "StatusGroup(code='$code')"

    class Builder {
        var code: String? = null

        fun build(): StatusGroup {
            val statusGroup = StatusGroup()

            statusGroup.code = code ?: throw IllegalArgumentException("code for StatusGroup cannot be null")

            return statusGroup
        }
    }
}
