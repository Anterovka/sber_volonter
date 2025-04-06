package sbp.com.sbt.dataspace.graphqlschema.datafetcher

import graphql.analysis.QueryTraverser
import graphql.introspection.Introspection.INTROSPECTION_SYSTEM_FIELDS
import graphql.language.Document
import graphql.language.Field
import graphql.language.OperationDefinition
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLSchema
import ru.sbertech.dataspace.security.graphql.SecurityRulesFetcher
import ru.sbertech.dataspace.security.model.dto.CheckSelect
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper

/**
 * Base class for introspection fetchers (__schema, __type).
 * There is FT in it to bring such requests under the usual security,
 * as well as functionality for performing a separate checkselect (without bringing it under the usual security)
 * for requests with exclusively __schema and __type fields.
 * TODO: Find a better way to intersect introspection queries after the document got parsed (right before DataFetchers?),
 *  because setting the INTROSPECTION_CHECK_SELECT_DONE for consequent invocations seems bad
 */
abstract class BaseIntrospectionDataFetcher(
    graphQLDataFetcherHelper: GraphQLDataFetcherHelper,
    securityRulesFetcher: SecurityRulesFetcher?,
    introspectionCheckCondition: String,
) : SecureDataFetcher(
        graphQLDataFetcherHelper,
        securityRulesFetcher,
    ) {
    private val introspectionCheckSelect: CheckSelect? =
        if (introspectionCheckCondition.isNotEmpty()) {
            CheckSelect().apply { conditionValue = introspectionCheckCondition }
        } else {
            null
        }

    /**
     * This method overrides the secureDataFetcher method, where security is handled before the main call.
     * If introspection requests are placed under security management (they require declaring SysOperation, as for a normal operation),
     * then we delegate it to the secureDataFetcher's source method with security.
     * If introspection requests are not covered by security management, then we simply use the basic get method.
     */
    override fun get(environment: DataFetchingEnvironment): Any {
        val isFullyIntrospection = isFullyIntrospectionRequest(environment.document, environment.graphQLSchema)
        val securityUsed = securityUsed(environment)
        val alreadyChecked = environment.graphQlContext.hasKey(INTROSPECTION_CHECK_SELECT_DONE)

        if (isFullyIntrospection and securityUsed and !alreadyChecked) {
            performCheckSelect(environment)
        }

        return get(environment, null)
    }

    private fun performCheckSelect(environment: DataFetchingEnvironment) {
        try {
            executeChecks(
                environment.variables,
                setOf(requireNotNull(introspectionCheckSelect) { "IntrospectionCheckSelect is absent, but check is requested" }),
            )
        } finally {
            environment.graphQlContext.put(INTROSPECTION_CHECK_SELECT_DONE, true)
        }
    }

    companion object {
        const val INTROSPECTION_CHECK_SELECT_DONE = "INTROSPECTION_CHECK_SELECT_DONE"

        /**
         * If all the fields (root) in the query are introspection fields, then we return true
         */
        private fun isFullyIntrospectionRequest(
            document: Document,
            schema: GraphQLSchema,
        ): Boolean {
            document
                .getFirstDefinitionOfType(OperationDefinition::class.java)
                .orElseThrow()
                .let {
                    return QueryTraverser
                        .newQueryTraverser()
                        .schema(schema)
                        .document(document)
                        .build()
                        .reducePreOrder({ fieldEnvironment, acc ->
                            // Get all root fields
                            if (fieldEnvironment.parentEnvironment == null) {
                                acc.add(fieldEnvironment.field)
                            }
                            acc
                        }, ArrayList<Field>())
                        .all { selection ->
                            // If all are the fields are introspection fields, then we return true
                            selection.name in INTROSPECTION_SYSTEM_FIELDS
                        }
                }
        }
    }
}
