package ru.sbertech.dataspace.uow.packet

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import ru.sbertech.dataspace.common.uncheckedCast
import ru.sbertech.dataspace.entitymanager.EntityManager
import ru.sbertech.dataspace.entitymanager.selector.Selector
import ru.sbertech.dataspace.expr.Expr
import ru.sbertech.dataspace.expr.dsl.ExprDsl.and
import ru.sbertech.dataspace.expr.dsl.expr
import ru.sbertech.dataspace.model.aggregates.Leaf
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.MappedReferenceCollectionProperty
import ru.sbertech.dataspace.model.property.ReferenceProperty
import ru.sbertech.dataspace.model.system.extension.IndexProperty
import ru.sbertech.dataspace.model.system.getIndex
import ru.sbertech.dataspace.model.system.requiredProperties
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.uow.command.Command
import ru.sbertech.dataspace.uow.command.CommandExecutionResult
import ru.sbertech.dataspace.uow.command.CommandVisitor
import ru.sbertech.dataspace.uow.command.ExternalReferencesCollection
import ru.sbertech.dataspace.uow.command.Increment
import ru.sbertech.dataspace.uow.command.LockMode
import ru.sbertech.dataspace.uow.command.Selection
import ru.sbertech.dataspace.uow.packet.aggregate.AggregateContext
import ru.sbertech.dataspace.uow.packet.aggregate.OptimisticLockContext
import ru.sbertech.dataspace.uow.packet.depends.DependsOnCommandHandler
import ru.sbertech.dataspace.uow.packet.idempotence.IdempotenceContext
import ru.sbertech.dataspace.uow.packet.idempotence.isRestored
import ru.sbertech.dataspace.uow.packet.status.StatusContext
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson
import java.sql.Connection

class CommandsExecutingVisitor(
    private val entityManager: EntityManager,
    private val commandResultByQualifier: HashMap<String, CommandExecutionResult>,
    private val idempotenceContext: IdempotenceContext?,
    private val aggregateContext: AggregateContext?,
    private val optimisticLockContext: OptimisticLockContext?,
    private val statusContext: StatusContext?,
    private val commandRefContext: CommandRefContext,
    private val entitiesReadAccessJson: EntitiesReadAccessJson,
    private val connection: Connection,
) : CommandVisitor<Unit> {
    private fun findEntityByIdentifier(
        selector: Selector,
        entityType: EntityType,
        identifier: UniversalValue,
    ): Map<String, UniversalValue> {
        val result =
            (entityManager.select(selector) as Collection<*>).takeIf { it.isNotEmpty() }
                ?: throw IllegalStateException(
                    "The Entity with type: ${entityType.name} and identifier: $identifier doesn't exists",
                )

        @Suppress("UNCHECKED_CAST")
        return result.iterator().next() as Map<String, UniversalValue>
    }

    private fun findEntityByIdentifierWithLock(
        selector: Selector,
        entityType: EntityType,
        identifier: UniversalValue,
    ): Map<String, UniversalValue> {
        entityManager.lock(entityType.name, identifier)
        val result =
            (entityManager.select(selector) as Collection<*>).takeIf { it.isNotEmpty() }
                ?: throw IllegalStateException(
                    "The Entity with type: ${entityType.name} and identifier: $identifier doesn't exists",
                )

        @Suppress("UNCHECKED_CAST")
        return result.iterator().next() as Map<String, UniversalValue>
    }

    private fun handleIncrements(
        entityType: EntityType,
        idPropertyName: String,
        identifier: UniversalValue,
        increments: List<Increment>,
        propertyValueByName: LinkedHashMap<String, UniversalValue?>,
    ) {
        if (increments.isEmpty()) {
            return
        }
        // TODO Если в propertyValueByName достаточно данных, то не делать запрос?
        val findById =
            Selector.EntityCollectionBased(
                entityType.name,
                increments.associate { it.fieldName to Selector.PropertyBased(it.fieldName) },
                expr { cur[idPropertyName] eq value(identifier) },
            )

        val entityFindResult = findEntityByIdentifierWithLock(findById, entityType, identifier)

        increments.forEach {
            val oldValue = propertyValueByName[it.fieldName] ?: entityFindResult[it.fieldName]
            propertyValueByName[it.fieldName] = it.execute(oldValue as Number?)
        }
    }

    private fun handleCompare(
        commandQualifier: String,
        propertyValueByNameForCompare: LinkedHashMap<String, UniversalValue?>,
        entityType: EntityType,
        idPropertyName: String,
        identifier: UniversalValue,
    ) {
        if (propertyValueByNameForCompare.isNotEmpty()) {
            commandRefContext.fillRefs(
                commandQualifier,
                propertyValueByNameForCompare,
                commandResultByQualifier,
            )

            val findById =
                Selector.EntityCollectionBased(
                    entityType.name,
                    propertyValueByNameForCompare.mapValues { Selector.PropertyBased(it.key) },
                    expr { cur[idPropertyName] eq value(identifier) },
                )
            val propertyValueByName = findEntityByIdentifierWithLock(findById, entityType, identifier)

            propertyValueByName.forEach { (propertyName, propertyActualValue) ->
                val propertyExpectedValue = propertyValueByNameForCompare[propertyName]
                if (propertyActualValue != propertyExpectedValue) {
                    throw IllegalStateException(
                        "Compare exception: actual property '$propertyName' value [$propertyActualValue] does not match with the expected" +
                            " value [$propertyExpectedValue]",
                    )
                }
            }
        }
    }

    private fun executeCommandSelection(
        selection: Selection?,
        propertyValueByName: LinkedHashMap<String, UniversalValue?>,
        idPropertyName: String,
        identifier: UniversalValue,
        entityType: EntityType,
        idempotenceContext: IdempotenceContext?,
    ): UniversalValue? {
        if (selection == null) {
            return null
        }
        // TODO LEGACY
//        val findByIdSelector = selection.build(expr { cur[idPropertyName] eq value(identifier) })

//        val selectionResultFillingVisitor =
//            SelectionResultFillingVisitor(
//                entityType,
//                propertyValueByName,
//                idempotenceContext?.restored ?: false,
//            )

        val selectionResult =
            // TODO LEGACY
//            findByIdSelector.accept(selectionResultFillingVisitor) ?:
            let {
                if (!idempotenceContext.isRestored) {
                    entityManager.flush()
                }

                // TODO LEGACY
//                findEntityByIdentifier(findByIdSelector, entityType, identifier)

                val findByIdQueryNode = selection.queryNode
                findByIdQueryNode.put("type", entityType.name)
                findByIdQueryNode.put("cond", "root.${'$'}id=='$identifier'")
                val entities = entitiesReadAccessJson.searchEntities(findByIdQueryNode, connection)

                val entity =
                    (entities as ObjectNode).get("elems").get(0) ?: throw IllegalStateException(
                        "The Entity with type: ${entityType.name} and identifier: $identifier doesn't exists",
                    )

                val entityAsMap = FeatherUtils.jsonToMap(entity as ObjectNode)

                val result = convertLegacyToNewFormat(entityAsMap)
                result as Map<*, *>
            }
        return selectionResult
    }

    private fun convertLegacyToNewFormat(value: Any?): Any? {
        when (value) {
            is Map<*, *> -> {
                if (value.containsKey("base") && value.containsKey("value")) {
                    return convertLegacyToNewFormat(value["value"])
                } else if (value.containsKey("id") && value.containsKey("type")) {
                    val newMap = linkedMapOf<String, Any?>()
                    newMap["_type"] = value["type"]!!
                    newMap["type"] = value["type"]!!
                    newMap["id"] = value["id"]!!
                    val properties = value["props"] as Map<String, Any?>?
                    properties?.also {
                        val convertedProperties = linkedMapOf<String, Any?>()

                        properties.forEach { (k, v) ->
                            convertedProperties[k] = convertLegacyToNewFormat(v)
                        }

                        newMap.putAll(convertedProperties)
                    }
                    return newMap
                } else if (value.containsKey("elems")) {
                    return convertLegacyToNewFormat(value["elems"])
                } else {
                    val newMap = linkedMapOf<String, Any?>()
                    value.forEach { (k, v) ->
                        newMap[k as String] = convertLegacyToNewFormat(v)
                    }
                    return newMap
                }
            }

            is Collection<*> -> {
                val newCollection = arrayListOf<Any?>()
                value.forEach { newCollection.add(convertLegacyToNewFormat(it)) }
                return newCollection
            }

            else -> {
                return value
            }
        }
    }

    private fun checkCommandProperties(
        entityType: EntityType,
        propertyValueByName: LinkedHashMap<String, UniversalValue?>,
    ) {
        propertyValueByName.forEach { (propertyName, propertyValue) ->
            propertyValue?.run {
                when (val property = entityType.inheritedPersistableProperty(propertyName)) {
                    is ReferenceProperty -> {
                        val referenceType = property.type
                        val findByIdSelector =
                            Selector.EntityCollectionBased(
                                referenceType.name,
                                mapOf("id" to Selector.PropertyBased(referenceType.tableIdProperty.name)),
                                expr { cur[referenceType.tableIdProperty.name] eq value(propertyValue) },
                            )
                        findEntityByIdentifier(findByIdSelector, referenceType, propertyValue)
                    }

                    is EmbeddedProperty -> {
                        val embeddedValues = propertyValue.uncheckedCast<Map<String, UniversalValue?>>()
                        property.embeddedType.embeddableType.requiredProperties.forEach { embeddableProperty ->
                            embeddedValues[embeddableProperty.name]
                                ?: throw IllegalArgumentException("Required property '$propertyName/${embeddableProperty.name}' is null")
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun handleExternalReferences(
        entityType: EntityType,
        parentEntityIdentifier: UniversalValue,
        propertyValueByName: LinkedHashMap<String, UniversalValue?>,
        isNewInstance: Boolean,
    ) {
        if (aggregateContext == null) {
            return
        }

        val externalCollectionsPropertiesToRemove = hashSetOf<String>()
        propertyValueByName.forEach { (propertyName, propertyValue) ->
            if (propertyValue != null) {
                val property = entityType.inheritedPersistableProperty(propertyName)
                if (property is MappedReferenceCollectionProperty) {
                    externalCollectionsPropertiesToRemove.add(propertyName)
                    val externalReferenceType = property.type
                    val externalReferencesCollection = propertyValue as ExternalReferencesCollection
                    val leaf = (aggregateContext.aggregatesModel.aggregateOrLeaf(externalReferencesCollection.entityType.name) as Leaf)
                    val backReferenceExpr = expr { cur[leaf.parentProperty.name] eq value(parentEntityIdentifier) }

                    if (!isNewInstance) {
                        val identifiersToDelete =
                            if (externalReferencesCollection.clear) {
                                // TODO поддержка ссылок в поисках
//                                findExternalReferencesIds(backReferenceExpr, externalReferenceType)
                                emptyList()
                            } else {
                                externalReferencesCollection.remove
                                    .distinct()
                                    .flatMap { externalReference ->
                                        externalReference
                                            .asSequence()
                                            .map {
                                                findExternalReferencesIds(
                                                    backReferenceExpr,
                                                    externalReferenceType,
                                                    it,
                                                ).firstOrNull()
                                            }.filterNotNull()
                                    }
                            }

                        identifiersToDelete.forEach { entityManager.delete(externalReferenceType.name, it) }
                    }

                    externalReferencesCollection.add.distinct().forEach createExternalReference@{ externalReference ->
                        if (!isNewInstance) {
                            if (externalReference
                                    .asSequence()
                                    .any {
                                        findExternalReferencesIds(
                                            backReferenceExpr,
                                            externalReferenceType,
                                            it,
                                        ).firstOrNull() != null
                                    }
                            ) {
                                return@createExternalReference
                            }
                        }

                        externalReference[leaf.parentProperty.name] = parentEntityIdentifier
                        externalReference[leaf.aggregateRootProperty.name] = aggregateContext.aggregateIdentifier
                        entityManager.create(externalReferenceType.name, externalReference)
                    }
                }
            }
        }

        externalCollectionsPropertiesToRemove.forEach { propertyValueByName.remove(it) }
    }

    private fun findExternalReferencesIds(
        backReferenceExpr: Expr,
        externalReferenceType: EntityType,
        externalReferenceAsEmbeddedProperty: Map.Entry<String, UniversalValue?>? = null,
    ): List<UniversalValue> {
        entityManager.flush()

        val externalReferenceExpr =
            externalReferenceAsEmbeddedProperty?.let {
                val referenceExpr = Expr.Property(Expr.Cur, externalReferenceAsEmbeddedProperty.key)
                (externalReferenceAsEmbeddedProperty.value as Map<String, Any>)
                    .asSequence()
                    .map {
                        Expr.Eq(Expr.Property(referenceExpr, it.key), Expr.Value(it.value))
                    }.reduce { acc: Expr, eq: Expr.Eq -> acc.and(eq) }
            }

        val selector =
            Selector.EntityCollectionBased(
                externalReferenceType.name,
                mapOf("id" to Selector.PropertyBased(externalReferenceType.tableIdProperty.name)),
                if (externalReferenceExpr == null) {
                    // TODO поддержка ссылок в поисках
//                  backReferenceExpr
                    Expr.Cur
                } else {
                    externalReferenceExpr
                    // TODO поддержка ссылок в поисках
//                    backReferenceExpr.and(externalReferenceExpr)
                },
            )

        return (entityManager.select(selector) as Collection<*>)
            .asSequence()
            .filterIsInstance<Map<String, UniversalValue>>()
            .map { it["id"] }
            .filterNotNull()
            .toList()
    }

    private fun getIndexExpression(
        property: IndexProperty,
        propertyValueByName: LinkedHashMap<String, UniversalValue?>,
    ) = expr {
        when (val parentPropertyName = property.parentPropertyName) {
            null -> {
                val propertyValue = (
                    propertyValueByName[property.name]
                        ?: throw IllegalStateException("byKey error: The value for the property '${property.name}' is not present")
                )
                cur[property.name] eq value(propertyValue)
            }

            else -> {
                val propertyValue =
                    (
                        propertyValueByName[parentPropertyName]?.uncheckedCast<Map<String, UniversalValue>>()?.get(
                            property.name,
                        )
                            ?: throw IllegalStateException(
                                "byKey error: The value for the property '$parentPropertyName.${property.name}' is not present",
                            )
                    )
                cur[parentPropertyName][property.name] eq value(propertyValue)
            }
        }
    }

    override fun visit(
        createCommand: Command.Create,
        param: Unit,
    ) {
        // TODO переписать с учётом embedded id
//        createCommand.entityType.rootEntityType.idProperty!!.name
        val entityType = createCommand.entityType
        val idPropertyName = entityType.tableIdProperty.name

        val idempotenceContext = idempotenceContext?.takeIf { createCommand.parent == null }

        var identifier = idempotenceContext?.let { createCommand.accept(it) }

        if (!DependsOnCommandHandler.isNeedExecuteCommand(createCommand, commandResultByQualifier)) {
            return
        }

        createCommand.takeIf { it.parent !is Command.UpdateOrCreate }?.also {
            commandRefContext.fillRefs(
                createCommand.qualifier,
                createCommand.propertyValueByName,
                commandResultByQualifier,
            )
        }

        if (identifier == null) {
            aggregateContext?.also { createCommand.accept(it) }
            checkCommandProperties(entityType, createCommand.propertyValueByName)
            statusContext?.also { createCommand.accept(it) }
            identifier = entityManager.create(entityType.name, createCommand.propertyValueByName)
            createCommand.propertyValueByName[idPropertyName] = identifier
            handleExternalReferences(entityType, identifier, createCommand.propertyValueByName, true)
            aggregateContext?.also { createCommand.accept(it) }
        }

        val selectionResult =
            executeCommandSelection(
                createCommand.selection,
                createCommand.propertyValueByName,
                idPropertyName,
                identifier,
                entityType,
                idempotenceContext,
            )

        when (createCommand.parent) {
            is Command.UpdateOrCreate -> { // do nothing
            }

            is Command.Many -> {
                val parent = createCommand.parent as Command.Many
                val commandExecutionResult =
                    commandResultByQualifier.getOrPut(parent.qualifier) {
                        CommandExecutionResult(
                            null,
                            mutableListOf<UniversalValue>(),
                            parent,
                        )
                    }

                @Suppress("UNCHECKED_CAST")
                val identifiers = commandExecutionResult.selectionResult as MutableCollection<UniversalValue>
                identifiers.add(identifier)
            }

            null -> {
                commandResultByQualifier[createCommand.qualifier] = CommandExecutionResult(identifier, selectionResult, createCommand)
            }

            else -> {
                throw IllegalStateException(
                    "Command with type '${createCommand.parent!!::class.simpleName}' cannot be parent " +
                        "for the command '${createCommand.qualifier}'",
                )
            }
        }

        idempotenceContext
            ?.takeIf { !it.restored }
            ?.addCommandResult(createCommand.qualifier, identifier)
    }

    override fun visit(
        updateCommand: Command.Update,
        param: Unit,
    ) {
        // TODO переписать с учётом embedded id
//        createCommand.entityType.rootEntityType.idProperty!!.name
        val entityType = updateCommand.entityType
        val idPropertyName = entityType.tableIdProperty.name

        val idempotenceContext = idempotenceContext?.takeIf { updateCommand.parent == null }

        idempotenceContext?.let { updateCommand.accept(it) }

        if (!DependsOnCommandHandler.isNeedExecuteCommand(updateCommand, commandResultByQualifier)) {
            return
        }

        commandRefContext.fillRefs(
            updateCommand.qualifier,
            updateCommand.propertyValueByName,
            commandResultByQualifier,
        )

        val identifier = updateCommand.propertyValueByName[idPropertyName] ?: throw IllegalStateException("The identifier is not set")

        if (!idempotenceContext.isRestored) {
            // TODO проверить, что в текущем пакете есть create нужной сущности и не делать лишний select и flush
            entityManager.flush()

            handleCompare(updateCommand.qualifier, updateCommand.propertyValueByNameForCompare, entityType, idPropertyName, identifier)
            handleIncrements(entityType, idPropertyName, identifier, updateCommand.increments, updateCommand.propertyValueByName)

            aggregateContext?.also { updateCommand.accept(it) }?.run {
                // TODO persistence context!
                if (updateCommand.propertyValueByNameForCompare.isEmpty()) {
                    val findByIdSelector =
                        Selector.EntityCollectionBased(
                            entityType.name,
                            mapOf("id" to Selector.PropertyBased(idPropertyName)),
                            expr { cur[idPropertyName] eq value(identifier) },
                        )

                    findEntityByIdentifier(findByIdSelector, entityType, identifier)
                }
            }
            statusContext?.also { updateCommand.accept(it) }

            checkCommandProperties(entityType, updateCommand.propertyValueByName)
            handleExternalReferences(entityType, identifier, updateCommand.propertyValueByName, false)
            entityManager.update(
                entityType.name,
                identifier,
                LinkedHashMap(updateCommand.propertyValueByName).also { it.remove(idPropertyName) },
            )
        }

        val selectionResult =
            executeCommandSelection(
                updateCommand.selection,
                updateCommand.propertyValueByName,
                idPropertyName,
                identifier,
                entityType,
                idempotenceContext,
            )

        when (updateCommand.parent) {
            is Command.UpdateOrCreate -> { // do nothing
            }

            is Command.Many -> {
                val parent = updateCommand.parent as Command.Many
                commandResultByQualifier.getOrPut(parent.qualifier) {
                    CommandExecutionResult(null, "success", parent)
                }
            }

            null -> {
                commandResultByQualifier[updateCommand.qualifier] = CommandExecutionResult(identifier, selectionResult, updateCommand)
            }

            else -> {
                throw IllegalStateException(
                    "Command with type '${updateCommand.parent!!::class.simpleName}' cannot be parent " +
                        "for the command '${updateCommand.qualifier}'",
                )
            }
        }

        idempotenceContext
            ?.takeIf { !it.restored }
            ?.addCommandResult(updateCommand.qualifier, "void")
    }

    override fun visit(
        updateOrCreateCommand: Command.UpdateOrCreate,
        param: Unit,
    ) {
        val entityType = updateOrCreateCommand.entityType
        val idPropertyName = entityType.tableIdProperty.name

        val idempotenceContext = idempotenceContext?.takeIf { updateOrCreateCommand.parent == null }

        @Suppress("UNCHECKED_CAST")
        val idempotenceResult = idempotenceContext?.let { updateOrCreateCommand.accept(it) } as Map<String, UniversalValue>?

        if (!DependsOnCommandHandler.isNeedExecuteCommand(updateOrCreateCommand, commandResultByQualifier)) {
            return
        }

        val selectionResult: UniversalValue?
        val identifier: UniversalValue
        val created: Boolean

        if (idempotenceResult == null) {
            entityManager.flush()

            commandRefContext.fillRefs(
                updateOrCreateCommand.qualifier,
                updateOrCreateCommand.createCommand.propertyValueByName,
                commandResultByQualifier,
            )

            val searchExpression: Expr =
                updateOrCreateCommand.createCommand.propertyValueByName[idPropertyName]?.let { expr { cur[idPropertyName] eq value(it) } }
                    ?: let {
                        val uniqueIndex =
                            updateOrCreateCommand.byKey?.let { uniqueIndexName -> entityType.getIndex(uniqueIndexName) }
                                ?: throw IllegalStateException("Id property value or unique index('byKey') must be defined")

                        uniqueIndex
                            .properties
                            .map { getIndexExpression(it, updateOrCreateCommand.createCommand.propertyValueByName) }
                            .reduce { finalExpression, expression -> finalExpression and expression }
                    }

            val findByIdSelector =
                Selector.EntityCollectionBased(
                    entityType.name,
                    mapOf(idPropertyName to Selector.PropertyBased(idPropertyName)),
                    searchExpression,
                )

            val findResult = entityManager.select(findByIdSelector) as Collection<*>

            if (findResult.isEmpty()) {
                updateOrCreateCommand.createCommand.accept(this)
                identifier =
                    updateOrCreateCommand.createCommand.propertyValueByName[idPropertyName]
                        ?: throw IllegalStateException("The identifier is not set")
                created = true
            } else {
                identifier = findResult.iterator().next()!!.uncheckedCast<Map<String, UniversalValue>>()[idPropertyName]!!
                updateOrCreateCommand.updateCommand.propertyValueByName[idPropertyName] = identifier
                updateOrCreateCommand.updateCommand.accept(this)
                created = false
            }

            idempotenceContext
                ?.takeIf { !it.restored }
                ?.addCommandResult(updateOrCreateCommand.qualifier, mapOf("id" to identifier, "created" to created))
        } else {
            identifier = idempotenceResult.getValue("id")
            created = (idempotenceResult.getValue("created") as String).toBoolean()
        }

        val propertyValueByName =
            if (created) {
                updateOrCreateCommand.createCommand.propertyValueByName
            } else {
                updateOrCreateCommand.updateCommand.propertyValueByName
            }

        propertyValueByName[idPropertyName] = identifier

        selectionResult =
            executeCommandSelection(
                updateOrCreateCommand.selection,
                propertyValueByName,
                idPropertyName,
                identifier,
                entityType,
                idempotenceContext,
            )

        val updateOrCreateSelectionResult = mapOf("created" to created, "returning" to selectionResult)

        if (updateOrCreateCommand.parent !is Command.Many) {
            commandResultByQualifier[updateOrCreateCommand.qualifier] =
                CommandExecutionResult(identifier, updateOrCreateSelectionResult, updateOrCreateCommand)
        } else {
            val commandExecutionResult =
                commandResultByQualifier.getOrPut(updateOrCreateCommand.parent.qualifier) {
                    CommandExecutionResult(null, mutableListOf<Map<String, UniversalValue>>(), updateOrCreateCommand.parent)
                }

            val results = commandExecutionResult.selectionResult as MutableCollection<UniversalValue>
            results.add(mapOf("id" to identifier, "created" to created))
        }
    }

    override fun visit(
        deleteCommand: Command.Delete,
        param: Unit,
    ) {
        val entityType = deleteCommand.entityType
        val idPropertyName = entityType.tableIdProperty.name
        val identifier =
            if (deleteCommand.identifier is String && deleteCommand.identifier.contains(REFERENCE)) {
                commandRefContext.fillSingleRef(deleteCommand.identifier, commandResultByQualifier)!!
            } else {
                deleteCommand.identifier
            }

        val idempotenceContext = idempotenceContext?.takeIf { deleteCommand.parent == null }

        idempotenceContext?.let { deleteCommand.accept(it) }

        if (!DependsOnCommandHandler.isNeedExecuteCommand(deleteCommand, commandResultByQualifier)) {
            return
        }

        if (!idempotenceContext.isRestored) {
            entityManager.flush()

            handleCompare(
                deleteCommand.qualifier,
                deleteCommand.propertyValueByNameForCompare,
                entityType,
                entityType.tableIdProperty.name,
                identifier,
            )

            // TODO подумать над copy
            aggregateContext?.also { deleteCommand.copy(identifier = identifier).accept(it) }?.run {
                if (deleteCommand.propertyValueByNameForCompare.isEmpty()) {
                    val findByIdSelector =
                        Selector.EntityCollectionBased(
                            entityType.name,
                            mapOf("id" to Selector.PropertyBased(idPropertyName)),
                            expr { cur[idPropertyName] eq value(identifier) },
                        )

                    findEntityByIdentifier(findByIdSelector, entityType, identifier)
                }
            }
            optimisticLockContext?.also { deleteCommand.copy(identifier = identifier).accept(it) }

            entityManager.delete(entityType.name, identifier)
        }

        commandResultByQualifier[deleteCommand.qualifier] = CommandExecutionResult(identifier, "success", deleteCommand)

        idempotenceContext
            ?.takeIf { !it.restored }
            ?.addCommandResult(deleteCommand.qualifier, "void")
    }

    override fun visit(
        getCommand: Command.Get,
        param: Unit,
    ) {
        entityManager.flush()

        idempotenceContext?.also { getCommand.accept(it) }

        var identifier = getCommand.identifier

        // TODO LEGACY
//        val idPropertyName = getCommand.entityType.tableIdProperty.name
        val idPropertyName = "id"
        val condition =
            if (identifier != null) {
                if (identifier is String && identifier.contains(REFERENCE)) {
                    identifier = commandRefContext.fillSingleRef(identifier, commandResultByQualifier)!!
                }
                // TODO LEGACY
//                expr { cur[idPropertyName] eq value(identifier) }
                "root.${'$'}id=='$identifier'"
            } else {
                getCommand.condition
            }

        var lockedId: UniversalValue? = null
        if (getCommand.lockMode != LockMode.NOT_USE) {
            try {
                val objectMapper = ObjectMapper()
                val queryNode = objectMapper.createObjectNode()
                queryNode.put("type", getCommand.entityType.name)
                queryNode.put("cond", condition)
                queryNode.put("lock", getCommand.lockMode.name.lowercase())
                val entities = entitiesReadAccessJson.searchEntities(queryNode, connection).get("elems")
                if (entities.size() > 1) {
                    throw IllegalStateException("Too many results")
                }
                if (!entities.isEmpty) lockedId = FeatherUtils.jsonToMap(entities.get(0).uncheckedCast()).getValue("id")
            } catch (_: Exception) {
                if (getCommand.failOnEmpty) {
                    throw IllegalStateException("Object is locked")
                }
            }
        }

        var selectionResult: List<Map<*, *>> = emptyList()
        if (getCommand.lockMode == LockMode.NOT_USE || lockedId != null) {
            val queryNode = getCommand.selection.queryNode
            queryNode.put("type", getCommand.entityType.name)
            queryNode.put("cond", condition)
            val entities = (entitiesReadAccessJson.searchEntities(queryNode, connection) as ObjectNode).get("elems")
            if (entities.size() > 1) {
                throw IllegalStateException("Too many results")
            }
            if (!entities.isEmpty) {
                val entityAsMap = FeatherUtils.jsonToMap(entities.get(0) as ObjectNode)
                val result = convertLegacyToNewFormat(entityAsMap) as Map<*, *>
                if (getCommand.lockMode == LockMode.NOT_USE || lockedId == entityAsMap.getValue("id")) {
                    selectionResult = arrayListOf(result)
                }
            }
        }

        // TODO LEGACY
//        val selector = getCommand.selection.build(condition) as Selector.EntityCollectionBased
//        (selector.selectorByName as MutableMap).getOrPut(idPropertyName) { Selector.PropertyBased(idPropertyName) }

//        val selectionResult = entityManager.select(selector) as Collection<*>

//        if (selectionResult.size > 1) {
//            throw IllegalStateException("Too many results")
//        }

        commandResultByQualifier[getCommand.qualifier] =
            if (selectionResult.isEmpty()) {
                if (getCommand.failOnEmpty) {
                    throw IllegalStateException("Object not found")
                }
                CommandExecutionResult(identifier, null, getCommand)
            } else {
                val singleResult = selectionResult.iterator().next()
                identifier = identifier ?: singleResult[idPropertyName]
                // TODO внутри лишний запрос в случае если entityType == Leaf, только для того, чтобы получить aggregateRoot
                aggregateContext?.also { getCommand.copy(identifier = identifier).accept(it) }
                CommandExecutionResult(identifier, singleResult, getCommand)
            }

        // TODO null строкой не очень
        idempotenceContext?.takeIf { !it.restored }?.addCommandResult(getCommand.qualifier, "null")
    }

    override fun visit(
        manyCommand: Command.Many,
        param: Unit,
    ) {
        val idempotenceResult = idempotenceContext?.let { manyCommand.accept(it) }

        if (!DependsOnCommandHandler.isNeedExecuteCommand(manyCommand, commandResultByQualifier)) {
            return
        }

        val result =
            if (idempotenceResult == null) {
                manyCommand.commands.forEach {
                    it.accept(this)
                }
                commandResultByQualifier[manyCommand.qualifier]?.selectionResult
            } else {
                val commandExecutionResult = CommandExecutionResult(null, idempotenceResult, manyCommand)
                commandResultByQualifier[manyCommand.qualifier] = commandExecutionResult
                commandExecutionResult.selectionResult
            } ?: throw IllegalStateException("Result for command '${manyCommand.qualifier}' is not found'")

        idempotenceContext
            ?.takeIf { !it.restored }
            ?.addCommandResult(manyCommand.qualifier, result)
    }
}
