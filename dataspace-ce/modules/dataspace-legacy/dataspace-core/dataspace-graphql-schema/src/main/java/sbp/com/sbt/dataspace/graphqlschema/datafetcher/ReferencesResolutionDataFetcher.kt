package sbp.com.sbt.dataspace.graphqlschema.datafetcher

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import graphql.GraphQLError
import graphql.execution.DataFetcherResult
import graphql.language.ArrayValue
import graphql.language.SelectionSet
import graphql.language.StringValue
import graphql.language.VariableReference
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJsonHelper
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription
import sbp.com.sbt.dataspace.graphqlschema.DataFetcherContainer
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper
import sbp.com.sbt.dataspace.graphqlschema.Helper
import sbp.com.sbt.dataspace.graphqlschema.InvalidDataError
import sbp.com.sbt.dataspace.graphqlschema.builder.GraphQLSchemaReferencesResolutionQueryBuilder

/** Used in federation so that stitching server can route the contents of the link */
class ReferencesResolutionDataFetcher(private val graphQLDataFetcherHelper: GraphQLDataFetcherHelper) :
    DataFetcher<Any> {

    companion object {
        const val INDEX_FIELD_NAME = "_index"
    }

    override fun get(environment: DataFetchingEnvironment): Any {
        lateinit var referenceType: String
        lateinit var ids: List<String>
        environment.field.arguments.forEach { argument ->
            when (argument.name) {
                GraphQLSchemaReferencesResolutionQueryBuilder.REFERENCE_TYPE_ARGUMENT_NAME ->
                    referenceType = graphQLDataFetcherHelper.getString(
                        DataFetcherContainer(environment, null, null),
                        argument.value
                    )
                GraphQLSchemaReferencesResolutionQueryBuilder.IDS_ARGUMENT_NAME -> {
                    ids = when (argument.value) {
                        is VariableReference ->
                            environment.variables[(argument.value as VariableReference).name] as List<String>
                        is ArrayValue ->
                            (argument.value as ArrayValue).values.map { element ->
                                if (element is VariableReference)
                                    environment.variables[element.name] as String
                                else
                                    (element as StringValue).value
                            }
else -> throw IllegalArgumentException("Invalid argument value for '${GraphQLSchemaReferencesResolutionQueryBuilder.IDS_ARGUMENT_NAME}'")
                    }
                }
            }
        }
        val entityType = getEntityType(referenceType)
        val requestNode = createRequestNode(environment, entityType, ids)
        val responseNode = graphQLDataFetcherHelper.entitiesReadAccessJson.searchEntities(requestNode)
        val entitiesById = ids.mapIndexed { index, id ->
            id to mutableMapOf<String, Any?>(
                INDEX_FIELD_NAME to index,
                Helper.TYPE to referenceType,
                GraphQLSchemaReferencesResolutionQueryBuilder.ENTITY_ID_FIELD_NAME to id
            )
        }.toMap(LinkedHashMap())
        val errors: MutableList<GraphQLError> = ArrayList()
        val elementsNode = responseNode.get(EntitiesReadAccessJsonHelper.ELEMENTS_FIELD_NAME)
        elementsNode.elements().forEachRemaining { entityNode ->
            val id = entityNode.get(EntitiesReadAccessJsonHelper.ID_FIELD_NAME).textValue()
            entitiesById[id]!![GraphQLSchemaReferencesResolutionQueryBuilder.ENTITY_FIELD_NAME] =
                graphQLDataFetcherHelper.getEntity(
                    errors,
                    listOf(
                        environment.field.name,
                        entitiesById[id]!![INDEX_FIELD_NAME],
                        GraphQLSchemaReferencesResolutionQueryBuilder.ENTITY_FIELD_NAME
                    ),
                    entityNode
                )
        }
        entitiesById.values.asSequence()
            .filter { GraphQLSchemaReferencesResolutionQueryBuilder.ENTITY_FIELD_NAME !in it }
            .forEach {
                errors += InvalidDataError(
                    it[GraphQLSchemaReferencesResolutionQueryBuilder.ENTITY_ID_FIELD_NAME] as String,
                    entityType,
                    listOf(
                        environment.field.name,
                        it[INDEX_FIELD_NAME],
                        GraphQLSchemaReferencesResolutionQueryBuilder.ENTITY_FIELD_NAME
                    )
                )
            }
        return DataFetcherResult.newResult<Any>()
            .data(entitiesById.values)
            .errors(errors)
            .build()
    }

    /**
* Get entity type by reference type [referenceType]
     */
    private fun getEntityType(referenceType: String): String {
        if (referenceType.startsWith(GraphQLSchemaReferencesResolutionQueryBuilder.ENTITY_REFERENCE_OBJECT_TYPE_PREFIX))
            return referenceType.substring(GraphQLSchemaReferencesResolutionQueryBuilder.ENTITY_REFERENCE_OBJECT_TYPE_PREFIX.length)
throw IllegalArgumentException("Incorrect reference type '$referenceType'")
    }

    /**
     * Process link
     * @param environment Environment
     * @param requestNode Request node
     * @param entityDescription Entity description
     * @param selectionSet The selective set
     */
    private fun processReference(
        environment: DataFetchingEnvironment,
        requestNode: ObjectNode,
        entityDescription: EntityDescription,
        selectionSet: SelectionSet
    ) {
        graphQLDataFetcherHelper.processSelectionSet(DataFetcherContainer(environment, null, null), selectionSet,
            { field ->
                if (field.name == GraphQLSchemaReferencesResolutionQueryBuilder.ENTITY_FIELD_NAME)
                    graphQLDataFetcherHelper.processProperties(
                        entityDescription,
                        requestNode,
                        graphQLDataFetcherHelper.getPropertiesNode(requestNode),
                        DataFetcherContainer(environment, null, null),
                        field.selectionSet
                    )
            },
            { inlineFragment ->
                graphQLDataFetcherHelper.checkMergeRequestSpecificationDirective(inlineFragment::getDirectives)
                processReference(environment, requestNode, entityDescription, inlineFragment.selectionSet)
            },
            { _, fragmentDefinition ->
                processReference(environment, requestNode, entityDescription, fragmentDefinition.selectionSet)
            }
        )
    }

    /**
     * Get the condition string for identifiers [ids]
     */
    private fun getConditionString(ids: List<String>): String {
        val stringBuilder = StringBuilder()
        var index = 0
        while (index < ids.size) {
            if (index != 0)
                stringBuilder.append("||")
            val startIndex = index
            val endIndex = index + 1000
            stringBuilder.append("it.\$id\$in[")
            while (index < ids.size && index < endIndex) {
                if (index != startIndex)
                    stringBuilder.append(',')
                stringBuilder.append('\'')
                stringBuilder.append(ids[index])
                stringBuilder.append('\'')
                ++index
            }
            stringBuilder.append(']')
        }
        return stringBuilder.toString()
    }

    /**
     * Create request node
     * @param environment Environment
     * @param entityType Entity type
     * @param ids Identifiers
     */
    private fun createRequestNode(
        environment: DataFetchingEnvironment,
        entityType: String,
        ids: List<String>
    ): JsonNode {
        val requestNode = Helper.OBJECT_MAPPER.createObjectNode()
        requestNode.put(EntitiesReadAccessJsonHelper.TYPE_FIELD_NAME, entityType)
        processReference(
            environment,
            requestNode,
            graphQLDataFetcherHelper.modelDescription.getEntityDescription(entityType),
            environment.field.selectionSet
        )
        requestNode.put(EntitiesReadAccessJsonHelper.CONDITION_FIELD_NAME, getConditionString(ids))
        requestNode.put(GraphQLSchemaHelper.SPECIAL_FLAG_FIELD_NAME, true)
        graphQLDataFetcherHelper.postProcessNode(requestNode)
        return requestNode
    }
}
