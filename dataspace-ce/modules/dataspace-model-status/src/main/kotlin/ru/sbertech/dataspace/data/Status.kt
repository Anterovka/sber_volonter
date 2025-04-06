package ru.sbertech.dataspace.data

import ru.sbertech.dataspace.model.type.EntityType

class Status {
    var isInitial: Boolean = false
    lateinit var code: String
        private set
    lateinit var group: StatusGroup
        private set
    lateinit var type: EntityType
        private set
    lateinit var transitions: List<StatusTransition>
        private set

    fun isTransitionDefined(statusTo: Status): Boolean = transitions.any { it.statusTo == statusTo }

    class Builder {
        var isInitial: Boolean = false
        var code: String? = null
        var groupCode: String? = null
        var transitions: MutableCollection<StatusTransition.Builder>? = null

        fun build(
            type: EntityType,
            statusGroups: List<StatusGroup>,
            statuses: List<Status>,
        ): Status {
            val status = Status()

            // TODO проверять, что по группе уже есть intial = true
            status.isInitial = isInitial
            status.code = code ?: throw IllegalArgumentException("code for Status cannot be null")
            status.group = statusGroups.firstOrNull { it.code == groupCode }
                ?: throw IllegalArgumentException(
                    "StatusGroup with code: '$groupCode' not found for Status with code: '$code' and type '$type'",
                )
            status.transitions = transitions?.map { it.build(status.group, statuses) } ?: emptyList()

            return status
        }
    }
}
