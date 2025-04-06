package ru.sbertech.dataspace.data

class StatusTransition {
    val statusTo: Status get() = statusToLazy.value

    private lateinit var statusToLazy: Lazy<Status>

    override fun toString(): String = "StatusTransition(statusTo=${statusTo.code})"

    class Builder {
        var statusTo: String? = null

        fun build(
            statusGroup: StatusGroup,
            statuses: List<Status>,
        ): StatusTransition {
            val statusTransition = StatusTransition()

            statusTransition.statusToLazy =
                lazy(LazyThreadSafetyMode.PUBLICATION) {
                    statuses.firstOrNull { it.code == statusTo && it.group == statusGroup }
                        ?: throw IllegalArgumentException("Status with code: '$statusTo' is not found")
                }

            return statusTransition
        }
    }
}
