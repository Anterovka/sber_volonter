package ru.sbertech.dataspace.uow.packet.aggregate

const val AGGREGATE_VERSION_REQUEST_ONLY = -1

data class AggregateVersion(
    val expectedAggregateVersion: Long?,
    private val isAggregateVersionRequestedInSelection: Boolean,
) {
    fun isRequested(): Boolean = expectedAggregateVersion != null || isAggregateVersionRequestedInSelection

    fun isNeedCheck(): Boolean = expectedAggregateVersion != null && expectedAggregateVersion.compareTo(AGGREGATE_VERSION_REQUEST_ONLY) != 0
}
