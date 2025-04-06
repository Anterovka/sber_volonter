package ru.sbertech.dataspace.security.admin

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.ParseAndValidate
import graphql.analysis.QueryTraverser
import graphql.analysis.QueryVisitor
import graphql.analysis.QueryVisitorFieldEnvironment
import graphql.analysis.QueryVisitorFragmentSpreadEnvironment
import graphql.analysis.QueryVisitorInlineFragmentEnvironment
import graphql.language.Field
import graphql.language.InlineFragment
import graphql.language.Node
import graphql.parser.Parser
import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLTypeUtil
import graphql.util.TraverserContext
import lombok.Getter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.sbertech.dataspace.common.exceptions.CompositeException
import ru.sbertech.dataspace.common.exceptions.ListException
import ru.sbertech.dataspace.common.exceptions.SingularDeepException
import ru.sbertech.dataspace.security.exception.AdminException
import ru.sbertech.dataspace.security.model.dto.CheckSelect
import ru.sbertech.dataspace.security.model.dto.Operation
import ru.sbertech.dataspace.security.model.dto.PathCondition
import ru.sbertech.dataspace.security.model.interfaces.SysCheckSelect
import ru.sbertech.dataspace.security.utils.GraphQLStringReplacer
import sbp.com.sbt.dataspace.feather.common.FeatherException
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJsonHelper
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.Optional

/**
 * Валидирует тело операции, условия безопасности (checkSelect, pathCondition), посылая проверочные запросы в feather,
 * а также проверяя корректность использования добавочных условий pathCondition
 */
class OperationValidator(
    private val schema: GraphQLSchema,
    private val entitiesReadAccessJson: EntitiesReadAccessJson, // TODO Legacy
) {
    @Getter
    private data class PathFragment(
        val node: Node<*>,
        val pathValue: String,
    )

    /** Проверяет тело запроса, checkSelect и pathCondition операций на наличие ошибок и при необходимости выбрасывает исключение  */
    fun checkOperations(operations: List<Operation>) {
        val compositeException = CompositeException("Ошибка проверки операций")
        operations.forEach {
            examineOperationChecks(it).ifPresent(compositeException::addException)
        }

        if (!compositeException.isEmpty) {
            throw AdminException(compositeException.getDeepMessage(), compositeException)
        }
    }

    /**
     * Проверяет тело запроса, условия checkSelects и pathConditions подставляя вместо переменных
     * произвольные значения соответствующего переменной типа и выполняя поиск.
     * Для pathConditions также происходят дополнительные проверки
     * в плане корректности их использования
     */
    private fun examineOperationChecks(operation: Operation): Optional<Throwable> {
        val exception = ListException()

        if (operation.name == null) {
            exception.addErrorMessage("Имя операции не может отсутствовать")
        }

        val checkSelects: Set<CheckSelect>? = operation.checkSelects
        val pathConditions: Map<String, PathCondition>? = operation.pathConditions

        // CheckSelect-ы проверяем на корректность условия
        if (!checkSelects.isNullOrEmpty()) {
            if (checkSelects.any { it.conditionValue == null }) {
                exception.addErrorMessage("Error when checking checkSelects: the condition cannot be null")
            }
            for (checkSelect in checkSelects.filter { it.conditionValue != null }) {
                // Если typeName не заполнен, то используем typeName по умолчанию (SysCheckSelect)
                // Если тип не указан, то не особо важно на каком из типов проверять условие, т.к.
                // анализируется только отсутствие ошибок поиска, а не результат поиска.
                val typeName =
                    when (val typeName = checkSelect.typeName) {
                        null, "" -> SysCheckSelect.NAME
                        else -> typeName
                    }
                // Накапливаем ошибки по checkSelects
                examineConditionCheck(checkSelect.conditionValue!!, typeName)
                    .ifPresent { s: String ->
                        exception
                            .addErrorMessage("Error when checking checkSelect: '" + checkSelect.conditionValue + "': " + s)
                    }
            }
        }

        val document = Parser.parse(operation.body)
        val bodyErrors = ParseAndValidate.validate(schema, document)
        // Лишний раз не гуляем по узлам AST (синтаксическое дерево) запроса
        if (bodyErrors.isEmpty()) {
            if (!pathConditions.isNullOrEmpty()) {
                // Additional conditions pathCondition, проверяем как на корректность условия,
                // так и на корректность их использования

                // Инициализируем тут всякое вспомогательное
                // [NonNull] map
                val pathConditionsMap =
                    pathConditions
                        .values
                        .associateBy({ requireNotNull(it.path) }, { requireNotNull(it.cond) })
                // [Unused] path (сформируются далее)
                val unusedPathConditions = HashSet(pathConditions.keys)

                // Проходимся по узлам запроса,
                // у них проходимся по аргументам и проверяем следующее:
                //      - валидность условия у корректно используемых pathConditions
                //      - pathConditions ссылаются только на поля, имеющие аргумент cond (не обязательно в текущем запросе, а в самой схеме), или являющиеся inlineFragment'ом у merge
                // потом ещё проверяем, что нет неиспользуемых, несуществующих и т.д. pathCondition'ов
                QueryTraverser
                    .newQueryTraverser()
                    .schema(schema)
                    .document(document)
                    .build()
                    .visitPreOrder(
                        object : QueryVisitor {
                            fun performPathConditionsCheck(
                                hasCond: Boolean,
                                path: String,
                                featherType: String,
                            ) {
                                // Если есть pathCondition'ы для этого пути
                                pathConditionsMap[path]?.apply {
                                    unusedPathConditions.remove(path)
                                    if (!hasCond) {
                                        exception.addErrorMessage(
                                            "Error checking the pathCondition: '$path' -> '$this': the main condition is missing in the specified field",
                                        )
                                    } else {
                                        examineConditionCheck(this, featherType)
                                            .ifPresent { s: String? ->
                                                exception.addErrorMessage(
                                                    "Error checking the pathCondition: '$path' -> '$this': $s",
                                                )
                                            }
                                    }
                                }
                            }

                            override fun visitField(queryVisitorFieldEnvironment: QueryVisitorFieldEnvironment) {
                                // Feather тип текущего поля для проверочного поиска
                                val type =
                                    GraphQLTypeUtil.unwrapAllAs<GraphQLNamedType>(queryVisitorFieldEnvironment.fieldDefinition.type)
                                val featherType = type.name.substring(type.name.lastIndexOf("_") + 1)
                                // pathCondition путь
                                val path =
                                    reduceToPath(queryVisitorFieldEnvironment.traverserContext)
                                        .joinToString(".") { it.pathValue }

                                val hasCond =
                                    queryVisitorFieldEnvironment
                                        .fieldDefinition
                                        .arguments
                                        .any { STR_COND == it.name }
                                performPathConditionsCheck(hasCond, path, featherType)
                            }

                            override fun visitInlineFragment(queryVisitorInlineFragmentEnvironment: QueryVisitorInlineFragmentEnvironment) {
                                // Feather тип текущего поля для проверочного поиска
                                val featherType =
                                    queryVisitorInlineFragmentEnvironment.inlineFragment.typeCondition.name
                                // pathCondition путь
                                val pathElements = reduceToPath(queryVisitorInlineFragmentEnvironment.traverserContext)
                                val path =
                                    pathElements
                                        .joinToString(".") { it.pathValue }

                                // Для фрагментов cond применим только если это inlineFragment у merge запроса
                                // Проверка на что-то типа merge.elems.Fragment
                                val hasCond =
                                    pathElements.size == 3 &&
                                        pathElements[0].node is Field &&
                                        MERGE_FIELD == (pathElements[0].node as Field).name
                                performPathConditionsCheck(hasCond, path, featherType)
                            }

                            override fun visitFragmentSpread(queryVisitorFragmentSpreadEnvironment: QueryVisitorFragmentSpreadEnvironment) {
                            }
                        },
                    )

                // Если после обхода осталось что-то нехорошее, ругаемся

                if (unusedPathConditions.isNotEmpty()) {
                    exception.addErrorMessage("The operation contains pathConditions for invalid paths: '$unusedPathConditions'")
                }
            }
        } else {
            bodyErrors.forEach { exception.addErrorMessage(it.message) }
        }
        return if (exception.isEmpty) {
            Optional.empty()
        } else {
            Optional.of(
                SingularDeepException(
                    "Ошибка при проверке операции " + operation.name,
                    exception,
                ),
            )
        }
    }

    /**
     * Проверяет, что если в строковом условии заменить все переменные соответствующими типу переменных
     * значениями, то поисковое условие отработает корректно.
     * Результат выборки не анализируется, проверяется только отсутствие ошибок поиска
     */
    private fun examineConditionCheck(
        condition: String,
        checkTypeName: String,
    ): Optional<String> {
        val requestNode = OBJECT_MAPPER.createObjectNode()
        requestNode.put(EntitiesReadAccessJsonHelper.TYPE_FIELD_NAME, checkTypeName)

        try {
            val conditionValue = processConditionVariables(condition)
            requestNode.put(EntitiesReadAccessJsonHelper.CONDITION_FIELD_NAME, conditionValue)

//            val dsToken: DataspaceAuthenticationToken = SecurityUtils.getCurrentTokenOrGuest()
            try {
//                dsToken.systemRead = java.lang.Boolean.TRUE
                entitiesReadAccessJson.searchEntities(requestNode)
            } catch (exception: FeatherException) {
                LOGGER.warn("Error during examineConditionCheck", exception)
                return Optional.of(exception.message!!)
            } finally {
//                dsToken.systemRead = java.lang.Boolean.FALSE
            }
            return Optional.empty()
        } catch (variablesParsingException: IllegalArgumentException) {
            return Optional.of(variablesParsingException.message!!)
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(OperationValidator::class.java)
        private val OBJECT_MAPPER: ObjectMapper = ObjectMapper()
        private val UTC_START: Instant = Instant.ofEpochMilli(86400000)

        /** Содержит для каждого простого типа пример значения в строковом представлении Feather  */
        private val TYPE_DEFAULT_VALUES: Map<String, String> =
            mapOf(
                "character" to "'a'",
                "string" to "'abc'",
                "byte" to "1",
                "short" to "1",
                "integer" to "1",
                "long" to "1",
                "float" to "1.0",
                "double" to "1.0",
                "bigdecimal" to "1",
                "localdate" to "D" + DateTimeFormatter.ISO_LOCAL_DATE.format(UTC_START.atZone(ZoneId.systemDefault())),
                "localdatetime" to "D" + DateTimeFormatter.ISO_LOCAL_TIME.format(UTC_START.atZone(ZoneId.systemDefault())),
                "offsetdatetime" to "D" + DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(UTC_START.atZone(ZoneId.systemDefault())),
                "date" to "D" + DateTimeFormatter.ISO_DATE.format(UTC_START.atZone(ZoneId.systemDefault())),
                "boolean" to "true",
            )
        private const val STR_COND: String = "cond"
        private const val MERGE_FIELD: String = "merge"

        /**
         * Обрабатывает подстановки значений по умолчанию в условии
         * @param condition условие для обработки
         * @return обработанное условие
         * @throws IllegalArgumentException если условие содержит некорректный синтаксис
         */
        private fun processConditionVariables(condition: String): String {
            return GraphQLStringReplacer.replaceAndReturn(condition) { replacementName: String ->
                val split = replacementName.split(":")
                if (split.size > 3) {
                    throw IllegalArgumentException("Too many colons in replacement $replacementName")
                }

                // Extracting the variable type from the variable name
                val type =
                    when (split.size) {
                        3 -> split[0]
                        2 ->
                            when (split[0]) {
                                "jwt", "req" -> "string"
                                else -> split[0]
                            }

                        else -> "string"
                    }

                val defaultValue: String =
                    if (type.endsWith("[]")) {
                        val typeWithoutBrackets = GraphQLStringReplacer.getType(type)
                        val elem =
                            TYPE_DEFAULT_VALUES[typeWithoutBrackets.lowercase(Locale.getDefault())]
                                ?: throw IllegalArgumentException(
                                    "Failed to determine type $typeWithoutBrackets in replacement $replacementName",
                                )

                        "[$elem,$elem]"
                    } else {
                        TYPE_DEFAULT_VALUES[type.lowercase(Locale.getDefault())]
                            ?: throw IllegalArgumentException("Failed to determine type $type in replacement $replacementName")
                    }

                return@replaceAndReturn GraphQLStringReplacer.SingleReplacement(defaultValue)
            }[0]
        }

        /**
         * Путь PathCondition текущего контекста обхода GQL документа.
         * Немного неэффективно, вычисляется на каждый узел полностью, сначала,
         * но для такого использования нестрашно
         *
         * @param context контекст с текущим узлом
         */
        private fun reduceToPath(context: TraverserContext<Node<*>>): List<PathFragment> {
            val nodesPath =
                context.parentNodes
                    .reversed() // Тут стек с узлами текущего пути
                    // Нужны только Field либо InlineFragment (на случай merge запроса)
                    .filter { it is Field || it is InlineFragment } + context.thisNode() // Добавляем текущий узел в конец

            // InlineFragment у merge включается в путь
            // Тут проверяем, что корень пути - merge запрос, тогда за ним будет следовать InlineFragment.
            // Его мы запоминаем, чтобы при соединении включить фрагмент в итоговый путь.
            // merge.elems.SomeInlineFragment
            //  [0]   [1]        [2]
            val root = nodesPath[0]
            val mergeSelection =
                if (root is Field && MERGE_FIELD == root.name && nodesPath.size > 2) {
                    nodesPath[2]
                } else {
                    null
                }

            // Соединяем оставшиеся узлы
            return nodesPath
                .map {
                    if (it is Field) {
                        return@map PathFragment(it, PathCondition.getPathPart(it))
                    } else { // instanceof InlineFragment
                        if (it === mergeSelection) {
                            return@map PathFragment(it, (it as InlineFragment).typeCondition.name)
                        }
                        return@map null
                    }
                }.filterNotNull()
        }
    }
}
