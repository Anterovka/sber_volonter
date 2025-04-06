package ru.sbertech.dataspace.security.graphql

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.execution.AbortExecutionException
import graphql.schema.DataFetchingEnvironment
import ru.sbertech.dataspace.common.onTrue
import ru.sbertech.dataspace.security.exception.AuthenticationException
import ru.sbertech.dataspace.security.model.dto.CheckSelect
import ru.sbertech.dataspace.security.model.dto.Operation
import ru.sbertech.dataspace.security.model.dto.PathCondition
import ru.sbertech.dataspace.security.model.interfaces.SysOperation
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson
import sbp.sbt.dataspacecore.security.utils.SecurityUtils

class DatabaseSecurityRulesFetcher(
    private val entitiesReadAccessJson: EntitiesReadAccessJson, // TODO Legacy
) : BaseSecurityRulesFetcher() {
    companion object {
        private val objectMapper = ObjectMapper()
        const val CHECK_SELECT_TYPE_NAME = "typeName"
        const val CHECK_SELECT_CONDITION_VALUE = "conditionValue"
        const val CHECK_SELECT_BEFORE_REQUEST_DISABLE = "beforeOperationDisable"
        const val CHECK_SELECT_BEFORE_COMMIT_ENABLE = "beforeCommitEnable"
        const val OPERATION_CHECK_SELECTS = "checkSelects"
        const val OPERATION_PATH_CONDITIONS = "pathConditions"
        const val OPERATION_HASH_VALUE = "hashValue"
        const val OPERATION_ALLOW_EMPTY_CHECKS = "allowEmptyChecks"
        const val OPERATION_DISABLE_JWT_VERIFICATION = "disableJwtVerification"
    }

    override fun getOperationInfo(environment: DataFetchingEnvironment): Operation {
        // Начинаем с того, что запросы без имени сразу идут к черту
        val operationName = environment.operationDefinition.name
        if (operationName.isNullOrEmpty()) {
            throw AbortExecutionException(
                AuthenticationException("Security Error. Anonymous transactions are prohibited"),
            )
        }

        // Запрос операции вместе с вложенным объектами (checkSelect)
        val searchNode = objectMapper.createObjectNode()
        searchNode.put("type", SysOperation.NAME)
        searchNode.put("cond", "it.\$id=='$operationName'")
        searchNode.set<JsonNode>(
            "props",
            objectMapper
                .createArrayNode()
                .add(OPERATION_HASH_VALUE)
                .add(OPERATION_ALLOW_EMPTY_CHECKS)
                .add(OPERATION_DISABLE_JWT_VERIFICATION)
                .add(OPERATION_PATH_CONDITIONS)
                .add(
                    objectMapper
                        .createObjectNode()
                        .set<JsonNode>(
                            "checkSelects",
                            objectMapper
                                .createObjectNode()
                                .put("type", "SysCheckSelect")
                                .set(
                                    "props",
                                    objectMapper
                                        .createArrayNode()
                                        .add(CHECK_SELECT_TYPE_NAME)
                                        .add(CHECK_SELECT_CONDITION_VALUE)
                                        .add(CHECK_SELECT_BEFORE_REQUEST_DISABLE)
                                        .add(CHECK_SELECT_BEFORE_COMMIT_ENABLE),
                                ),
                        ),
                ),
        )
        searchNode.put("count", true)

        // Выполняем поиск как системный поиск (без влияния безопасности)
        val jsonNode: JsonNode
        val authentication = SecurityUtils.getCurrentToken()
        val systemRead = authentication.systemRead
        try {
            authentication.systemRead = true
            jsonNode = entitiesReadAccessJson.searchEntities(searchNode)
        } finally {
            authentication.systemRead = systemRead
        }

        if (jsonNode.get("count").asInt() == 0) {
            throw AbortExecutionException(
                AuthenticationException("Security Error. Request with the name $operationName missing from the allowed list."),
            )
        }

        val props = jsonNode.get("elems").get(0)?.get("props")
        return Operation().apply {
            hash = props!!.get(OPERATION_HASH_VALUE).asText()
            allowEmptyChecks = props.get(OPERATION_ALLOW_EMPTY_CHECKS).asBoolean()
            disableJwtVerification = props.get(OPERATION_DISABLE_JWT_VERIFICATION).asBoolean()
            props.hasNonNull(OPERATION_PATH_CONDITIONS).onTrue {
                pathConditions =
                    PathCondition.asMap(
                        objectMapper.readValue(
                            props.get(OPERATION_PATH_CONDITIONS).asText(),
                            object : TypeReference<List<PathCondition>>() {},
                        ),
                    )
            }
            props.hasNonNull(OPERATION_CHECK_SELECTS).onTrue {
                checkSelects =
                    props
                        .get(OPERATION_CHECK_SELECTS)
                        .get("elems")
                        .map { checkSelectNode ->
                            val checkSelectProps = checkSelectNode.get("props")
                            CheckSelect().apply {
                                typeName = checkSelectProps.get(CHECK_SELECT_TYPE_NAME).asText()
                                conditionValue = checkSelectProps.get(CHECK_SELECT_CONDITION_VALUE).asText()
                                // Skip those, they are irrelevant for security
                                // description
                                // orderValue
                            }
                        }.toSet()
            }
        }
    }
}
