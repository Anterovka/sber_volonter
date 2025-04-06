package ru.sbertech.dataspace.sql

data class Lock(
    val mode: LockMode = LockMode.FOR_UPDATE,
)
