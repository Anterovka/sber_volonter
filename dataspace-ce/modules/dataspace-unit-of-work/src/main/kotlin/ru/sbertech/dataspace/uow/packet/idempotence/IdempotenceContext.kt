package ru.sbertech.dataspace.uow.packet.idempotence

import ru.sbertech.dataspace.entitymanager.EntityManager
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.uow.command.Command
import ru.sbertech.dataspace.uow.command.CommandVisitor
import ru.sbertech.dataspace.uow.packet.aggregate.AggregateContext
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson
import java.sql.Connection

data class CommandIdempotenceData(
    val paramsHash: String,
    val result: UniversalValue,
)

val IdempotenceContext?.isRestored get() = this != null && this.restored

class IdempotenceContext private constructor(
    private val idempotenceId: String,
    private val commandIdempotenceData: ArrayList<CommandIdempotenceData>,
    private val entityManager: EntityManager,
    private val aggregateContext: AggregateContext,
    val restored: Boolean,
) : CommandVisitor<UniversalValue?> {
    private val commandHashByQualifier = hashMapOf<String, String>()
    private var idempotenceEntryIndex = 0

    companion object {
        fun create(
            idempotenceId: String?,
            commands: Collection<Command>,
            aggregateContext: AggregateContext?,
            entityManager: EntityManager,
            entitiesReadAccessJson: EntitiesReadAccessJson,
            connection: Connection,
        ): IdempotenceContext? {
            if (idempotenceId == null || aggregateContext == null) {
                return null
            }

            val restoredIdempotenceData =
                IdempotenceDataDao.read(
                    entityManager,
                    idempotenceId,
                    aggregateContext,
                    entitiesReadAccessJson,
                    connection,
                )

            return if (restoredIdempotenceData.isEmpty()) {
                IdempotenceContext(idempotenceId, restoredIdempotenceData, entityManager, aggregateContext, false)
            } else {
                if (restoredIdempotenceData.size != commands.size) {
                    throw IllegalStateException(
                        "Commands count(${commands.size}) doesn't match with commands count(${restoredIdempotenceData.size}) of the previous call",
                    )
                }
                IdempotenceContext(idempotenceId, restoredIdempotenceData, entityManager, aggregateContext, true)
            }
        }
    }

    private fun computeAndCheckCommandHash(
        command: Command,
        commandIdempotenceEntry: CommandIdempotenceData?,
    ) {
        val paramsHash = CommandHashComputer.compute(command)

        if (commandIdempotenceEntry == null) {
            commandHashByQualifier[command.qualifier] = paramsHash
        } else {
            if (commandIdempotenceEntry.paramsHash != paramsHash) {
                throw IllegalArgumentException(
                    "The hash parameters of the command doesn't match with the hash of the previous idempotence call",
                )
            }
        }
    }

    fun addCommandResult(
        commandQualifier: String,
        result: UniversalValue,
    ) {
        val paramsHash =
            commandHashByQualifier[commandQualifier]
                ?: throw IllegalStateException("Parameters hash for command $commandQualifier doesn't exists")
        commandIdempotenceData.add(CommandIdempotenceData(paramsHash, result))
    }

    fun flush() {
        if (!restored) {
            IdempotenceDataDao.write(entityManager, idempotenceId, aggregateContext, commandIdempotenceData)
        }
    }

    override fun visit(
        createCommand: Command.Create,
        param: Unit,
    ): UniversalValue? {
        val commandIdempotenceEntry = commandIdempotenceData.getOrNull(idempotenceEntryIndex++)
        computeAndCheckCommandHash(createCommand, commandIdempotenceEntry)

        return if (commandIdempotenceEntry == null) {
            null
        } else {
            val id = commandIdempotenceEntry.result
            createCommand.propertyValueByName[createCommand.entityType.tableIdProperty.name] = id
            id
        }
    }

    override fun visit(
        updateCommand: Command.Update,
        param: Unit,
    ) {
        val commandIdempotenceEntry = commandIdempotenceData.getOrNull(idempotenceEntryIndex++)
        computeAndCheckCommandHash(updateCommand, commandIdempotenceEntry)
    }

    override fun visit(
        updateOrCreateCommand: Command.UpdateOrCreate,
        param: Unit,
    ): UniversalValue? {
        val commandIdempotenceEntry = commandIdempotenceData.getOrNull(idempotenceEntryIndex++)
        computeAndCheckCommandHash(updateOrCreateCommand, commandIdempotenceEntry)

        return commandIdempotenceEntry?.result
    }

    override fun visit(
        deleteCommand: Command.Delete,
        param: Unit,
    ) {
        val commandIdempotenceEntry = commandIdempotenceData.getOrNull(idempotenceEntryIndex++)
        computeAndCheckCommandHash(deleteCommand, commandIdempotenceEntry)
    }

    override fun visit(
        getCommand: Command.Get,
        param: Unit,
    ) {
        val commandIdempotenceEntry = commandIdempotenceData.getOrNull(idempotenceEntryIndex++)
        computeAndCheckCommandHash(getCommand, commandIdempotenceEntry)
    }

    override fun visit(
        manyCommand: Command.Many,
        param: Unit,
    ): UniversalValue? {
        val commandIdempotenceEntry = commandIdempotenceData.getOrNull(idempotenceEntryIndex++)
        computeAndCheckCommandHash(manyCommand, commandIdempotenceEntry)

        return commandIdempotenceEntry?.result
    }
}
