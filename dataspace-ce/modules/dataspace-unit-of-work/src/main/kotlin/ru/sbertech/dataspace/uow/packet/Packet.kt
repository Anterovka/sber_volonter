package ru.sbertech.dataspace.uow.packet

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.sbertech.dataspace.entitymanager.EntityManager
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.uow.command.Command
import ru.sbertech.dataspace.uow.command.CommandExecutionResult
import ru.sbertech.dataspace.uow.packet.aggregate.AggregateContext
import ru.sbertech.dataspace.uow.packet.aggregate.AggregateVersion
import ru.sbertech.dataspace.uow.packet.aggregate.OptimisticLockContext
import ru.sbertech.dataspace.uow.packet.idempotence.IdempotenceContext
import ru.sbertech.dataspace.uow.packet.idempotence.isRestored
import ru.sbertech.dataspace.uow.packet.status.StatusContext
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson
import java.sql.Connection

data class PacketExecutionResult(
    val isIdempotenceResponse: Boolean,
    val aggregateVersion: Long,
    val commandResultByQualifier: Map<String, CommandExecutionResult>,
)

class Packet(
    private val model: Model,
    private val idempotenceId: String?,
    private val aggregateVersion: AggregateVersion,
    private val entityManager: EntityManager,
    private val commandByQualifier: LinkedHashMap<String, Command>,
    private val commandRefContext: CommandRefContext,
    private val entitiesReadAccessJson: EntitiesReadAccessJson,
    private val isManyAggregatesAllowed: Boolean = false,
) {
    fun execute(connection: Connection): PacketExecutionResult {
        val commands = commandByQualifier.values
        val aggregateContext =
            AggregateContext.create(commands, model, entityManager, entitiesReadAccessJson, connection, isManyAggregatesAllowed)
        val optimisticLockContext =
            OptimisticLockContext.create(aggregateVersion, aggregateContext, entityManager)
        val idempotenceContext =
            IdempotenceContext.create(
                idempotenceId,
                commands,
                aggregateContext,
                entityManager,
                entitiesReadAccessJson,
                connection,
            )

        val statusContext = StatusContext.create(model, entityManager)

        val commandResultByQualifier = hashMapOf<String, CommandExecutionResult>()
        val commandsExecutingVisitor =
            CommandsExecutingVisitor(
                entityManager,
                commandResultByQualifier,
                idempotenceContext,
                aggregateContext,
                optimisticLockContext,
                statusContext,
                commandRefContext,
                entitiesReadAccessJson,
                connection,
            )

        commands.forEach {
            try {
                it.accept(commandsExecutingVisitor)
            } catch (e: Exception) {
                LOGGER.error(e.stackTraceToString())
                throw IllegalStateException("Command '${it.qualifier}' execution error: ${e.message}")
            }
        }

        optimisticLockContext?.flush()
        idempotenceContext?.flush()
        entityManager.flush()

        return PacketExecutionResult(
            idempotenceContext.isRestored,
            optimisticLockContext?.currentAggregateVersion ?: 0,
            commandResultByQualifier,
        )
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(Packet::class.java)
    }
}
