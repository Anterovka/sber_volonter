package ru.sbertech.dataspace.uow.command

interface CommandVisitor<out R> : CommandParameterizedVisitor<Unit, R>
