package ru.sbertech.dataspace.uow.command

enum class LockMode {
    NOT_USE,
    WAIT,
    NOWAIT,
}
