package ru.sbertech.dataspace.security.admin

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import ru.sbertech.dataspace.entitymanager.selector.Selector
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.primitive.Text
import ru.sbertech.dataspace.security.model.dto.CheckSelect
import ru.sbertech.dataspace.security.model.dto.Operation
import ru.sbertech.dataspace.security.model.dto.PathCondition
import ru.sbertech.dataspace.security.model.interfaces.SysCheckSelect
import ru.sbertech.dataspace.security.model.interfaces.SysOperation
import ru.sbertech.dataspace.security.utils.GraphQLHashHelper
import ru.sbertech.dataspace.security.utils.GraphQLStringReplacer
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.uow.command.Command
import ru.sbertech.dataspace.uow.command.CommandExecutionResult
import ru.sbertech.dataspace.uow.command.LockMode
import ru.sbertech.dataspace.uow.command.Selection
import ru.sbertech.dataspace.uow.packet.CommandRefContext
import ru.sbertech.dataspace.uow.packet.Packet
import ru.sbertech.dataspace.uow.packet.aggregate.AggregateVersion
import ru.sbertech.dataspace.uow.packet.depends.DependsOn
import ru.sbertech.dataspace.util.ContextHelper
import sbp.sbt.dataspacecore.security.utils.JsonHelper
import java.util.concurrent.atomic.AtomicInteger

// TODO: Need to ensure correct errors are resolved (not found, already exists, so on) + tests
class GraphQlSecurityAdminService(
    private val operationValidator: OperationValidator,
    private val addAsReplace: Boolean,
    private val contextHelper: ContextHelper,
) {
    private val sysOperationEntityType: EntityType = contextHelper.model.type(SYS_OPERATION) as EntityType
    private val sysCheckSelectEntityType: EntityType = contextHelper.model.type(SYS_CHECK_SELECT) as EntityType

    // TODO Legacy (на entityManager переделать, когда появится cond, page, offset)
    fun searchOperations(
        name: String?,
        page: Int?,
        pageSize: Int?,
    ): List<Operation> {
        val searchSpecNode = getOperationSearchNodeStub()

        if (name != null) {
            searchSpecNode.put("cond", "it.\$id\$like" + GraphQLStringReplacer.screenString(name))
        }
        if (page != null && pageSize != null) {
            searchSpecNode.put(LIMIT, pageSize)
            searchSpecNode.put(OFFSET, pageSize * (page - 1))
        }
        searchSpecNode.set<JsonNode>(
            "sort",
            OBJECT_MAPPER
                .createArrayNode()
                .add(
                    OBJECT_MAPPER
                        .createObjectNode()
                        .put("crit", "it.\$id")
                        .put("order", "asc"),
                ),
        )

        return contextHelper.withEntityManagerContext {
            try {
                // SecurityUtils.getCurrentToken().systemRead = true

                val searchResult: JsonNode = entitiesReadAccessJson.searchEntities(searchSpecNode)

                val result = OBJECT_MAPPER.createArrayNode()
                if (!searchResult.has(ELEMS)) {
                    return@withEntityManagerContext emptyList()
                }
                for (elem in searchResult.get(ELEMS)) {
                    val operationNode = OBJECT_MAPPER.createObjectNode()
                    val props: JsonNode =
                        elem.get(PROPS)
                    operationNode.set<JsonNode>(NAME, elem["id"])
                    operationNode.set<JsonNode>(BODY, props.get(BODY))
                    operationNode.set<JsonNode>(ALLOW_EMPTY_CHECKS, props.get(ALLOW_EMPTY_CHECKS))
                    operationNode.set<JsonNode>(DISABLE_JWT_VERIFICATION, props.get(DISABLE_JWT_VERIFICATION))

                    val pathConditions = convertPathConditions(props.get(PATH_CONDITIONS))
                    if (!pathConditions.isEmpty) {
                        operationNode.set<JsonNode>(PATH_CONDITIONS, pathConditions)
                    }
                    val checkSelects: JsonNode =
                        props
                            .get(CHECK_SELECTS)
                            .get(ELEMS)
                    if (!checkSelects.isEmpty) {
                        val resultCheckSelects = OBJECT_MAPPER.createArrayNode()
                        for (checkSelect in checkSelects) {
                            val resultCheckSelect = OBJECT_MAPPER.createObjectNode()
                            resultCheckSelect.set<JsonNode>(
                                ID,
                                checkSelect.get("id"),
                            )

                            val checkSelectProps: JsonNode = checkSelect.get(PROPS)
                            resultCheckSelect.set<JsonNode>(
                                CHECK_SELECT_CONDITION_VALUE,
                                checkSelectProps.get(CHECK_SELECT_CONDITION_VALUE),
                            )
                            resultCheckSelect.set<JsonNode>(
                                CHECK_SELECT_TYPE_NAME,
                                checkSelectProps.get(CHECK_SELECT_TYPE_NAME),
                            )
                            resultCheckSelect.set<JsonNode>(
                                CHECK_SELECTS_DESCRIPTION,
                                checkSelectProps.get(CHECK_SELECTS_DESCRIPTION),
                            )
                            resultCheckSelects.add(resultCheckSelect)
                        }
                        operationNode.set<JsonNode>(
                            CHECK_SELECTS,
                            resultCheckSelects,
                        )
                    }
                    result.add(operationNode)
                }

                return@withEntityManagerContext OBJECT_MAPPER.treeToValue(result, object : TypeReference<List<Operation>>() {})
            } finally {
                // SecurityUtils.getCurrentToken().systemRead = false
            }
        }
    }

    fun createOperation(operation: Operation) {
        if (addAsReplace) {
            replaceOperation(operation)
            return
        }

        checkSingleOperation(operation)
        executePacket(
            getCreateOperationCommands(AtomicInteger(0), operation),
        )
    }

    fun createOperations(operations: List<Operation>) {
        checkMultipleOperations(operations)
        executePacket(
            with(AtomicInteger(0)) {
                // For each operation create commands
                operations.map { operation ->
                    getCreateOperationCommands(this, operation)
                }
                // Then flatMap to single map
            }.flatMap { it.entries }.associateTo(LinkedHashMap()) { it.key to it.value },
        )
    }

    fun deleteOperation(operationName: String) {
        executePacket(
            getDeleteOperationCommand(AtomicInteger(0), operationName, true),
        )
    }

    fun deleteOperations() {
        executePacket(
            with(AtomicInteger(0)) {
                // Find all operations
                searchOperations(null, null, null)
                    .map { requireNotNullName(it) }
                    // Create a delete command for each
                    .map { getDeleteOperationCommand(this, it, false) }
                    .flatMap { it.entries }
                    .associateTo(LinkedHashMap()) { it.key to it.value }
            },
        )
    }

    fun mergeOperations(operations: List<Operation>) {
        contextHelper.withEntityManagerContext {
            // Lock them before merging with the passed ones and validating
            operations.forEach {
                entityManager.lock(SYS_OPERATION, requireNotNullName(it))
            }

            val operationsToMergeNames =
                operations
                    .associateBy { it.name }

            val operationsMerged =
                searchOperations(null, null, null)
                    .filter { it.name in operationsToMergeNames }
                    .onEach {
                        it.body = operationsToMergeNames[it.name]!!.body
                    }

            // Check that operations are valid (considering pathConditions) with their new bodies
            checkMultipleOperations(operationsMerged)

            // Then perform the update using current context
            with(AtomicInteger(0)) {
                executePacket(
                    this@withEntityManagerContext,
                    operations
                        .map { operation ->
                            getUpdateOrCreateOperationBodyOnlyCommand(this, operation)
                        }.associateByTo(LinkedHashMap()) { it.qualifier },
                )
            }
        }
    }

    fun replaceOperation(operation: Operation) {
        checkSingleOperation(operation)
        executePacket(
            with(AtomicInteger(0)) {
                LinkedHashMap<String, Command>().apply {
                    // Delete operation
                    putAll(getDeleteOperationCommand(this@with, requireNotNullName(operation), false))
                    // Create operation
                    putAll(getCreateOperationCommands(this@with, operation))
                }
            },
        )
    }

    fun replaceOperations(operations: List<Operation>) {
        checkMultipleOperations(operations)
        executePacket(
            with(AtomicInteger(0)) {
                operations
                    .map { operation ->
                        LinkedHashMap<String, Command>().apply {
                            // Delete operation
                            putAll(getDeleteOperationCommand(this@with, requireNotNullName(operation), false))
                            // Create operation
                            putAll(getCreateOperationCommands(this@with, operation))
                        }
                    }.flatMap { it.entries }
                    .associateTo(LinkedHashMap()) { it.key to it.value }
            },
        )
    }

    fun replaceAllOperations(operations: List<Operation>) {
        deleteOperations()
        createOperations(operations)
    }

    /**
     * Проверка по схеме
     */
    private fun checkSingleOperation(operation: Operation) {
        checkMultipleOperations(listOf(operation))
    }

    /**
     * Проверка по схеме
     */
    private fun checkMultipleOperations(operations: List<Operation>) {
        operationValidator.checkOperations(operations)
    }

    private fun getDeleteOperationCommand(
        qualifier: AtomicInteger,
        operationName: String,
        failOnEmpty: Boolean,
    ): LinkedHashMap<String, Command> =
        if (!failOnEmpty) {
            with(qualifier.getAndIncrement().toString()) {
                linkedMapOf(
                    this to
                        Command.Get(
                            this,
                            sysOperationEntityType,
                            operationName,
                            null,
                            Selection(
                                SYS_OPERATION,
                                mapOf(
                                    ID to Selector.PropertyBased(ID),
                                ),
                                // TODO LEGACY
                                OBJECT_MAPPER
                                    .createObjectNode()
                                    .set("props", OBJECT_MAPPER.createArrayNode()),
                            ),
                            false,
                            LockMode.NOT_USE,
                            emptyList(),
                        ),
                    qualifier.toString() to
                        Command.Delete(
                            qualifier.getAndIncrement().toString(),
                            sysOperationEntityType,
                            operationName,
                            linkedMapOf(),
                            listOf(
                                DependsOn.Get(
                                    this,
                                    DependsOn.Dependency.EXISTS,
                                ),
                            ),
                        ),
                ).also {
                    // TODO Убрать also, когда добавят автоматическое удаление дочерних сущностей, при удалении родительской
                    searchOperations(operationName, null, null).getOrNull(0)?.run {
                        checkSelects
                            ?.map {
                                Command.Delete(
                                    qualifier.getAndIncrement().toString(),
                                    sysCheckSelectEntityType,
                                    requireNotNull(it.id),
                                    linkedMapOf(),
                                    listOf(
                                        DependsOn.Get(
                                            this@with,
                                            DependsOn.Dependency.EXISTS,
                                        ),
                                    ),
                                )
                            }?.associateBy { it.qualifier }
                            ?.run { it.putAll(this) }
                    }
                }
            }
        } else {
            linkedMapOf<String, Command>(
                qualifier.toString() to
                    Command.Delete(
                        qualifier.getAndIncrement().toString(),
                        sysOperationEntityType,
                        operationName,
                        linkedMapOf(),
                        emptyList(),
                    ),
            ).also {
                // TODO Убрать also, когда добавят автоматическое удаление дочерних сущностей, при удалении родительской
                searchOperations(operationName, null, null).getOrNull(0)?.run {
                    checkSelects
                        ?.map {
                            Command.Delete(
                                qualifier.getAndIncrement().toString(),
                                sysCheckSelectEntityType,
                                requireNotNull(it.id),
                                linkedMapOf(),
                                emptyList(),
                            )
                        }?.associateBy { it.qualifier }
                        ?.run { it.putAll(this) }
                }
            }
        }

    private fun getCreateOperationCommands(
        qualifier: AtomicInteger,
        operation: Operation,
    ): LinkedHashMap<String, Command> =
        (
            // SysOperation
            listOf(
                Command.Create(
                    qualifier.getAndIncrement().toString(),
                    sysOperationEntityType,
                    null,
                    operationAsMap(operation),
                    emptyList(),
                ),
            ) +
                // then CheckSelects
                (
                    operation.checkSelects?.map { checkSelect ->
                        Command.Create(
                            qualifier.getAndIncrement().toString(),
                            sysCheckSelectEntityType,
                            null,
                            checkSelectAsMap(requireNotNullName(operation), checkSelect),
                            emptyList(),
                        )
                    } ?: emptyList()
                )
        ).associateByTo(LinkedHashMap()) { it.qualifier }

    private fun getUpdateOrCreateOperationBodyOnlyCommand(
        qualifier: AtomicInteger,
        operation: Operation,
    ): Command =
        with(qualifier.getAndIncrement().toString()) {
            Command.UpdateOrCreate(
                this,
                sysOperationEntityType,
                Command.Create(
                    this,
                    sysOperationEntityType,
                    null,
                    operationAsMapBodyOnly(operation),
                    emptyList(),
                ),
                Command.Update(
                    this,
                    sysOperationEntityType,
                    null,
                    operationAsMapBodyOnly(operation),
                    linkedMapOf(),
                    emptyList(),
                    emptyList(),
                ),
                null,
                emptyList(),
            )
        }

    /**
     * Automatically handled context
     */
    private fun executePacket(commands: LinkedHashMap<String, Command>) {
        contextHelper.withEntityManagerContext {
            executePacket(this, commands)
        }
    }

    /**
     * Manually handled context
     */
    private fun executePacket(
        context: ContextHelper.Companion.Context,
        commands: LinkedHashMap<String, Command>,
    ) {
        context.run {
            Packet(
                model,
                null,
                AggregateVersion(null, false),
                entityManager,
                commands,
                object : CommandRefContext {
                    override fun registerReference(commandQualifier: String) {
//                        TODO("NO-OP, Replace with concrete context implementation")
                    }

                    override fun fillRefs(
                        commandQualifier: String,
                        propertyValueByName: HashMap<String, UniversalValue?>,
                        commandResultByQualifier: HashMap<String, CommandExecutionResult>,
                    ) {
//                        TODO("NO-OP, Replace with concrete context implementation")
                    }

                    override fun fillSingleRef(
                        refExpression: String,
                        commandResultByQualifier: HashMap<String, CommandExecutionResult>,
                    ): UniversalValue? {
                        return null
//                        TODO("NO-OP, Replace with concrete context implementation")
                    }
                },
                entitiesReadAccessJson,
            ).execute(connection)
        }
    }

    companion object {
        private val OBJECT_MAPPER: ObjectMapper = ObjectMapper()

        private const val SYS_OPERATION: String = SysOperation.NAME
        private const val SYS_CHECK_SELECT: String = SysCheckSelect.NAME

        private const val LIMIT: String = "limit"
        private const val OFFSET: String = "offset"
        private const val PROPS: String = "props"
        private const val ELEMS: String = "elems"

        private const val ROOT_SECURITY: String = "rootSecurity"

        private const val HASH_VALUE: String = "hashValue"
        private const val ID: String = "objectId"
        private const val BODY: String = "body"
        private const val NAME: String = "name"
        private const val DISABLE_JWT_VERIFICATION: String = "disableJwtVerification"
        private const val ALLOW_EMPTY_CHECKS: String = "allowEmptyChecks"
        private const val PATH_CONDITIONS: String = "pathConditions"
        private const val CHECK_SELECTS: String = "checkSelects"
        private const val CHECK_SELECT_OPERATION: String = "operation"
        private const val CHECK_SELECT_ORDER: String = "orderValue"
        private const val CHECK_SELECT_TYPE_NAME: String = "typeName"
        private const val CHECK_SELECT_CONDITION_VALUE: String = "conditionValue"
        private const val CHECK_SELECTS_DESCRIPTION: String = "description"

        private fun requireNotNullName(operation: Operation): String = requireNotNull(operation.name) { "Operation name cannot be null" }

        private fun operationAsMap(operation: Operation): LinkedHashMap<String, UniversalValue?> =
            linkedMapOf(
                ROOT_SECURITY to "1",
                HASH_VALUE to GraphQLHashHelper.calculateHash(operation),
                ID to operation.name,
                BODY to operation.body?.let { Text(it) },
                DISABLE_JWT_VERIFICATION to operation.disableJwtVerification,
                ALLOW_EMPTY_CHECKS to operation.allowEmptyChecks,
                PATH_CONDITIONS to
                    operation.pathConditions
                        ?.let { pathCondition ->
                            OBJECT_MAPPER.valueToTree<JsonNode>(pathCondition.values).toString()
                        }?.let { Text(it) },
            )

        private fun checkSelectAsMap(
            operationId: String,
            checkSelect: CheckSelect,
        ): LinkedHashMap<String, UniversalValue?> =
            linkedMapOf(
                CHECK_SELECT_OPERATION to operationId,
                CHECK_SELECT_ORDER to checkSelect.orderValue,
                CHECK_SELECT_TYPE_NAME to checkSelect.typeName,
                CHECK_SELECT_CONDITION_VALUE to checkSelect.conditionValue?.let { Text(it) },
                CHECK_SELECTS_DESCRIPTION to checkSelect.description,
            )

        private fun operationAsMapBodyOnly(operation: Operation): LinkedHashMap<String, UniversalValue?> =
            linkedMapOf(
                ID to operation.name,
                ROOT_SECURITY to "1",
                HASH_VALUE to GraphQLHashHelper.calculateHash(operation),
                BODY to operation.body?.let { Text(it) },
            )

        private fun convertPathConditions(node: JsonNode): JsonNode {
            try {
                return OBJECT_MAPPER.valueToTree(
                    OBJECT_MAPPER.readValue<List<PathCondition>>(
                        node.asText(),
                        object : TypeReference<List<PathCondition>?>() {},
                    ),
                )
            } catch (e: JsonProcessingException) {
                throw RuntimeException("Ошибка десериализации путевых условий")
            }
        }

        // TODO Legacy
        private fun getOperationSearchNodeStub(): ObjectNode =
            JsonHelper.OBJECT_MAPPER
                .createObjectNode()
                .put("type", SYS_OPERATION)
                .set(
                    PROPS,
                    JsonHelper.OBJECT_MAPPER
                        .createArrayNode()
                        .add(BODY)
                        .add(HASH_VALUE)
                        .add(ALLOW_EMPTY_CHECKS)
                        .add(DISABLE_JWT_VERIFICATION)
                        .add(PATH_CONDITIONS)
                        .add(
                            JsonHelper.OBJECT_MAPPER.createObjectNode().set<JsonNode>(
                                CHECK_SELECTS,
                                JsonHelper.OBJECT_MAPPER
                                    .createObjectNode()
                                    .set<ObjectNode>(
                                        PROPS,
                                        JsonHelper.OBJECT_MAPPER
                                            .createArrayNode()
                                            .add(CHECK_SELECT_CONDITION_VALUE)
                                            .add(CHECK_SELECT_TYPE_NAME)
                                            .add(CHECK_SELECTS_DESCRIPTION),
                                    ).set(
                                        "sort",
                                        JsonHelper.OBJECT_MAPPER
                                            .createArrayNode()
                                            .add(
                                                JsonHelper.OBJECT_MAPPER
                                                    .createObjectNode()
                                                    .put("crit", "it.orderValue")
                                                    .put("order", "asc"),
                                            ),
                                    ),
                            ),
                        ),
                )
    }
}
