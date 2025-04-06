package sbp.com.sbt.dataspace.graphqlschema.datafetcher

import com.fasterxml.jackson.databind.JsonNode
import graphql.execution.AbortExecutionException
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.util.StringUtils
import ru.sbertech.dataspace.security.graphql.SecurityRulesFetcher
import ru.sbertech.dataspace.security.model.dto.CheckSelect
import ru.sbertech.dataspace.security.model.interfaces.SysRootSecurity
import ru.sbertech.dataspace.security.utils.GraphQLSecurityContext
import ru.sbertech.dataspace.security.utils.GraphQLStringReplacer
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJsonHelper
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper
import sbp.com.sbt.dataspace.graphqlschema.Helper
import sbp.sbt.dataspacecore.security.utils.SecurityUtils
import sbp.sbt.dataspacecore.security.utils.SecurityUtils.Companion.GRAPHQL_CONTEXT_SECURE_ENABLE_NAME

abstract class SecureDataFetcher(
    @JvmField protected val graphQLDataFetcherHelper: GraphQLDataFetcherHelper,
    private val securityRulesFetcher: SecurityRulesFetcher? = null,
) : DataFetcher<Any> {
    companion object {
        const val FAILED_CHECK_SELECT = "Security Error. The operation did not pass the security check."
    }

    abstract fun get(
        environment: DataFetchingEnvironment,
        securityContext: GraphQLSecurityContext?,
    ): Any

    protected fun securityUsed(environment: DataFetchingEnvironment): Boolean =
        true == environment.graphQlContext.get<Boolean?>(GRAPHQL_CONTEXT_SECURE_ENABLE_NAME)

    override fun get(environment: DataFetchingEnvironment): Any {
        if (!securityUsed(environment)) {
            return get(environment, GraphQLSecurityContext())
        }

        val securityContext = checkSecurityAndReadVariables(environment)
        return get(environment, securityContext)
    }

    protected fun executeChecks(
        variables: MutableMap<String, Any>,
        checkSelects: Set<CheckSelect>,
    ) {
        val authentication = SecurityUtils.getCurrentToken()

        try {
            authentication.systemRead = true
            checkSelects.forEach { check ->
                var typeName = check.typeName
                if (!StringUtils.hasLength(typeName) || typeName == "null") {
                    typeName = SysRootSecurity.NAME
                }

                val variableCheckSelects =
                    GraphQLStringReplacer.replaceAndReturn(
                        check.conditionValue!!,
                        variables,
                        authentication.attributes,
                    )

                variableCheckSelects.forEach {
                    val securityResult: JsonNode =
                        graphQLDataFetcherHelper.entitiesReadAccessJson.searchEntities(
                            getRequestNode(typeName!!, it),
                        )
                    if (securityResult[GraphQLSchemaHelper.ELEMENTS_FIELD_NAME].isEmpty) {
                        throw AbortExecutionException(FAILED_CHECK_SELECT)
                    }
                }
            }
        } finally {
            authentication.systemRead = false
        }
    }

    private fun checkSecurityAndReadVariables(environment: DataFetchingEnvironment): GraphQLSecurityContext {
        // If the token allows everything, or for some reason related to the configuration, we cannot read the rules, then
        // we consider security to be off
        val authentication = SecurityUtils.getCurrentToken()
        if (authentication.isSuppressGqlCheck) {
            return GraphQLSecurityContext()
        }

        if (securityRulesFetcher == null) {
            throw SecurityException("There is no any securityRulesFetcher")
        }

        val secOperation = securityRulesFetcher.getSecurityRules(environment)
        if (!secOperation.disableJwtVerification && !authentication.isAuthenticated) {
            throw SecurityException("Not authorized. Operation ${environment.operationDefinition.name}")
        }

        val allGraphQLVariables = environment.variables
        return GraphQLSecurityContext(allGraphQLVariables, secOperation).apply {
            // perform checks
            secOperation.checkSelects?.run {
                executeChecks(allGraphQLVariables, this)
            }
        }
    }

    private fun getRequestNode(
        type: String,
        conditionValue: String,
    ): JsonNode {
        val requestNode = Helper.OBJECT_MAPPER.createObjectNode()
        requestNode.put(EntitiesReadAccessJsonHelper.TYPE_FIELD_NAME, type)
        requestNode.put(EntitiesReadAccessJsonHelper.CONDITION_FIELD_NAME, conditionValue)
        return requestNode
    }
}
