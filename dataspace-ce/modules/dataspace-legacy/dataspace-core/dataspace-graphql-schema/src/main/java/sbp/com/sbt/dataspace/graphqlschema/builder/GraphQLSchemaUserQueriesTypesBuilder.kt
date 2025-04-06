package sbp.com.sbt.dataspace.graphqlschema.builder

import com.sbt.dataspace.pdm.PdmModel
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import org.springframework.stereotype.Component
import ru.sbertech.dataspace.security.model.helper.SecurityClassesHolder
import ru.sbertech.dataspace.security.utils.TypeMappingHelper
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription
import sbp.com.sbt.dataspace.feather.modeldescription.TableType
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaSettings
import sbp.com.sbt.dataspace.graphqlschema.Helper
import java.util.Locale

@Component
class GraphQLSchemaUserQueriesTypesBuilder(
    modelDescription: ModelDescription,
    private val pdmModel: PdmModel,
    private val graphQLSchemaSettings: GraphQLSchemaSettings,
) : GraphQLSchemaModelDescriptionAwareQueryBuilder(modelDescription) {
    override fun build(
        queryTypeBuilder: GraphQLObjectType.Builder,
        additionalTypes: MutableSet<GraphQLType>,
        additionalDirectives: MutableSet<GraphQLDirective>,
        codeRegistryBuilder: GraphQLCodeRegistry.Builder,
    ) {
        filteredEntityDescriptions {
            it.tableType == TableType.QUERY && !SecurityClassesHolder.isSecurityClass(it.name)
        }.forEach { entityDescription ->
            val fieldDefinitions = ArrayList<GraphQLFieldDefinition>()

            // We add only columns from the user request, there are no additional fields and there cannot be any
            entityDescription.primitiveDescriptions.forEach { prop ->
                fieldDefinitions.add(
                    GraphQLFieldDefinition
                        .newFieldDefinition()
                        .name(prop.key)
                        .type(
                            Helper.getType(
                                Helper.TYPE_MAPPING[prop.value.type],
                                false,
                            ),
                        ).build(),
                )
            }

            if (entityDescription.idColumnName != null) {
                fieldDefinitions.add(
                    GraphQLFieldDefinition
                        .newFieldDefinition()
                        .name("id")
                        .type(
                            Helper.getType(
                                Helper.TYPE_MAPPING[TypeMappingHelper.TYPES_MAPPING["string"]],
                                true,
                            ),
                        ).build(),
                )
            }

            addCalcFields(graphQLSchemaSettings, fieldDefinitions)

            // Define the type name _Q_+Query name
            val typeName = GraphQLSchemaHelper.SQL_QUERY_OBJECT_TYPE_PREFIX + entityDescription.name

            // Create a GQL object type
            val entityObjectTypeBuilder: GraphQLObjectType.Builder =
                GraphQLObjectType
                    .newObject()
                    .name(typeName)
                    .fields(fieldDefinitions)

            // Build the type and add it to the collection
            val entityObjectType = entityObjectTypeBuilder.build()
            additionalTypes.add(entityObjectType)

            // Create a collection type (elems, count)
            val entitiesCollectionObjectType =
                GraphQLObjectType
                    .newObject()
                    .name(GraphQLSchemaHelper.ENTITIES_COLLECTION_OBJECT_TYPE_PREFIX + entityDescription.name)
                    .field(
                        GraphQLFieldDefinition
                            .newFieldDefinition()
                            .name(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME)
                            .type(GraphQLNonNull.nonNull(GraphQLList.list(GraphQLNonNull.nonNull(entityObjectType)))),
                    ).field(Helper.COUNT_FIELD_DEFINITION)
                    .build()

            // Add the collection type to the list of types
            additionalTypes.add(entitiesCollectionObjectType)

            addExtendedPropertyDataFetchers(
                entityDescription,
                typeName,
                codeRegistryBuilder,
                fieldDefinitions,
            )

            // If there are no parameters in the request, then we do nothing
            if (entityDescription.paramDescriptions.isNotEmpty()) {
                // Create a type for input parameters
                val fields = mutableListOf<GraphQLInputObjectField>()
                entityDescription.paramDescriptions.forEach { param ->
                    val type =
                        if (param.component2().isCollection) {
                            GraphQLList(Helper.TYPE_MAPPING[param.component2().type])
                        } else {
                            Helper.TYPE_MAPPING[param.component2().type] as GraphQLInputType
                        }
                    fields.add(
                        GraphQLInputObjectField
                            .Builder()
                            .name(param.component1())
                            .type(type)
                            .build(),
                    )
                }
                val paramsInputObjectType =
                    GraphQLInputObjectType
                        .Builder()
                        .name(
                            GraphQLSchemaHelper.SQL_QUERY_PARAMS_TYPE_PREFIX +
                                entityDescription.name.replaceFirstChar { it.titlecase(Locale.getDefault()) } +
                                GraphQLSchemaHelper.SQL_QUERY_PARAMS_TYPE_SUFFIX,
                        ).fields(fields)
                        .build()

                additionalTypes.add(paramsInputObjectType)
            }
        }
    }
}
