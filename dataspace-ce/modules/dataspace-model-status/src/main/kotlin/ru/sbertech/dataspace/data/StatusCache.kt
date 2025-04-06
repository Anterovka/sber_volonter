package ru.sbertech.dataspace.data

data class StatusCache(
    val statusesByGroup: Map<String, List<Status>>,
)
