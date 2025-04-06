package ru.sbertech.dataspace.uow.command

interface CommandParameterizedVisitor<in P, out R> {
    fun visit(
        createCommand: Command.Create,
        param: P,
    ): R

    fun visit(
        updateCommand: Command.Update,
        param: P,
    ): R

    fun visit(
        deleteCommand: Command.Delete,
        param: P,
    ): R

    fun visit(
        updateOrCreateCommand: Command.UpdateOrCreate,
        param: P,
    ): R

    fun visit(
        getCommand: Command.Get,
        param: P,
    ): R

    fun visit(
        manyCommand: Command.Many,
        param: P,
    ): R
}
