package sbp.com.sbt.dataspace.graphqlschema.builder

import com.sbt.dataspace.pdm.PdmModel
import com.sbt.mg.jpa.JpaConstants
import graphql.Scalars
import graphql.scalars.ExtendedScalars
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull.nonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference.typeRef
import org.springframework.stereotype.Component
import ru.sbertech.dataspace.security.model.helper.SecurityClassesHolder
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription
import sbp.com.sbt.dataspace.feather.modeldescription.TableType
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.ENTITIES_COLLECTION_OBJECT_TYPE_PREFIX
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.ENTITY_INTERFACE_TYPE_NAME
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.ENTITY_OBJECT_TYPE_PREFIX
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.ENUM_COLLECTION_OBJECT_TYPE_PREFIX
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.ENUM_OBJECT_TYPE_PREFIX
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.GROUP_OBJECT_TYPE_PREFIX
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaSettings
import sbp.com.sbt.dataspace.graphqlschema.Helper
import sbp.com.sbt.dataspace.graphqlschema.Helper.ALIAS_ARGUMENT
import sbp.com.sbt.dataspace.graphqlschema.Helper.COLLECTION_TYPE_MAPPING
import sbp.com.sbt.dataspace.graphqlschema.Helper.DEFAULT_SEARCH_SPECIFICATION_ARGUMENTS
import sbp.com.sbt.dataspace.graphqlschema.Helper.ELEMENT_ALIAS_ARGUMENT
import sbp.com.sbt.dataspace.graphqlschema.Helper.TYPE_MAPPING
import sbp.com.sbt.dataspace.graphqlschema.Helper.getType

@Component
class GraphQLSchemaSearchTypesBuilder(
    modelDescription: ModelDescription,
    private val graphQLSchemaSettings: GraphQLSchemaSettings,
    private val pdmModel: PdmModel,
) : GraphQLSchemaModelDescriptionAwareQueryBuilder(modelDescription) {
    override fun build(
        queryTypeBuilder: GraphQLObjectType.Builder,
        additionalTypes: MutableSet<GraphQLType>,
        additionalDirectives: MutableSet<GraphQLDirective>,
        codeRegistryBuilder: GraphQLCodeRegistry.Builder,
    ) {
        filteredEntityDescriptions {
            it.tableType != TableType.QUERY && !SecurityClassesHolder.isSecurityClass(it.name)
        }.forEach { entityDescription ->
            val fieldDefinitions = ArrayList<GraphQLFieldDefinition>()
            // creating a search method (field) search<EntityName>(...)
            fieldDefinitions.add(
                GraphQLFieldDefinition
                    .newFieldDefinition()
                    .name(graphQLSchemaSettings.idFieldName)
                    .type(nonNull(Scalars.GraphQLID))
                    .build(),
            )

            // If the inheritance root is an aggregate (but not a Directory), then add the aggVersion field
            if (entityDescription.rootEntityDescription
                    .let { root -> root.aggregateEntityDescription ?: root }
                    .let { it.isAggregate && it.name != GraphQLDataFetcherHelper.getRootDictionaryClassName() }
            ) {
                fieldDefinitions.add(
                    GraphQLFieldDefinition
                        .newFieldDefinition()
                        .name(graphQLSchemaSettings.aggregateVersionFieldName)
                        .type(nonNull(ExtendedScalars.GraphQLLong))
                        .build(),
                )
            }
            // Add primitive fields (including enum fields)
            entityDescription.primitiveDescriptions.values.forEach { primitiveDescription ->
                fieldDefinitions.add(
                    GraphQLFieldDefinition
                        .newFieldDefinition()
                        .name(primitiveDescription.name)
                        .type(
                            if (primitiveDescription.enumDescription == null) {
                                getType(TYPE_MAPPING[primitiveDescription.type], primitiveDescription.isMandatory)
                            } else {
                                getType(
                                    typeRef(ENUM_OBJECT_TYPE_PREFIX + primitiveDescription.enumDescription.name),
                                    primitiveDescription.isMandatory,
                                )
                            },
                        ).build(),
                )
            }
            // Add collection fields, adding search arguments to them (cond, limit, offset, sort)
            entityDescription.primitivesCollectionDescriptions.values.forEach { primitivesCollectionDescription ->
                fieldDefinitions.add(
                    GraphQLFieldDefinition
                        .newFieldDefinition()
                        .name(primitivesCollectionDescription.name)
                        .arguments(DEFAULT_SEARCH_SPECIFICATION_ARGUMENTS)
                        .type(
                            if (primitivesCollectionDescription.enumDescription == null) {
                                COLLECTION_TYPE_MAPPING[primitivesCollectionDescription.type]
                            } // notNull because the collection can be empty but cannot be null
                            else {
                                nonNull(
                                    typeRef(
                                        ENUM_COLLECTION_OBJECT_TYPE_PREFIX + primitivesCollectionDescription.enumDescription.name,
                                    ),
                                )
                            },
                        ).build(),
                )
            }
            // Add reference fields to which the alias argument is added
            val isEntityHistoryClass = pdmModel.model.getClass(entityDescription.name)?.isHistoryClass ?: false
            entityDescription.referenceDescriptions.values.forEach { referenceDescription ->
                /**
                 * Problem: GQL ignores valid data if there is an object history that references a deleted object.
                 * Solution: Set the mandatory property of the sysHistoryOwner field in the GQL schema to false.
                 */
                val isMandatory: Boolean =
                    if (isEntityHistoryClass && JpaConstants.HISTORY_OWNER_PROPERTY == referenceDescription.name) {
                        false
                    } else {
                        referenceDescription.isMandatory
                    }
                fieldDefinitions.add(
                    GraphQLFieldDefinition
                        .newFieldDefinition()
                        .name(referenceDescription.name)
                        .argument(ALIAS_ARGUMENT)
                        .type(getType(typeRef(referenceDescription.entityDescription.name), isMandatory))
                        .build(),
                )
            }
            // Add backlinks with the alias argument
            entityDescription.referenceBackReferenceDescriptions.values.forEach { backReferenceDescription ->
                fieldDefinitions.add(
                    GraphQLFieldDefinition
                        .newFieldDefinition()
                        .name(backReferenceDescription.entityReferencePropertyName)
                        .argument(ALIAS_ARGUMENT)
                        .type(typeRef(backReferenceDescription.ownerEntityDescription.name))
                        .build(),
                )
            }
            // Link collections, alias arguments + search set (cond, limit, offset, sort)
            entityDescription.referencesCollectionDescriptions.values.forEach { referencesCollectionDescription ->
                fieldDefinitions.add(
                    GraphQLFieldDefinition
                        .newFieldDefinition()
                        .name(referencesCollectionDescription.name)
                        .argument(ELEMENT_ALIAS_ARGUMENT)
                        .arguments(DEFAULT_SEARCH_SPECIFICATION_ARGUMENTS)
                        .type(
                            nonNull(
                                typeRef(
                                    ENTITIES_COLLECTION_OBJECT_TYPE_PREFIX + referencesCollectionDescription.entityDescription.name,
                                ),
                            ),
                        ).build(),
                )
            }
            // Backlink collections, alias arguments + search set (cond, limit, offset, sort)
            entityDescription.referencesCollectionBackReferenceDescriptions.values.forEach { backReferenceDescription ->
                fieldDefinitions.add(
                    GraphQLFieldDefinition
                        .newFieldDefinition()
                        .name(backReferenceDescription.entityReferencesCollectionPropertyName)
                        .argument(ELEMENT_ALIAS_ARGUMENT)
                        .arguments(DEFAULT_SEARCH_SPECIFICATION_ARGUMENTS)
                        .type(
                            nonNull(typeRef(ENTITIES_COLLECTION_OBJECT_TYPE_PREFIX + backReferenceDescription.ownerEntityDescription.name)),
                        ).build(),
                )
            }
            // Embedded fields
            entityDescription.groupDescriptions.values.forEach { groupDescription ->
                fieldDefinitions.add(
                    GraphQLFieldDefinition
                        .newFieldDefinition()
                        .name(groupDescription.name)
                        .type(nonNull(typeRef(GROUP_OBJECT_TYPE_PREFIX + groupDescription.groupName)))
                        .build(),
                )
            }

            // Calculation fields are added: _getString, _getDouble, etc.
            addCalcFields(graphQLSchemaSettings, fieldDefinitions)

            // Define the type name _E_+Name of the type
            val typeName = ENTITY_OBJECT_TYPE_PREFIX + entityDescription.name

            // Attaches a universal DataFetcher to non-primitive (e.g. reference) fields of the type
            // I didn't quite understand how it works
            // Ideally, this method should be in SearchQueryBuilder,
            // but it turned out to be difficult to move it out because of the connection to fieldDefinitions
            addExtendedPropertyDataFetchers(
                entityDescription,
                typeName,
                codeRegistryBuilder,
                fieldDefinitions,
            )

            // Create a GQL object type
            val entityObjectTypeBuilder: GraphQLObjectType.Builder =
                GraphQLObjectType
                    .newObject()
                    .name(typeName)
                    .withInterface(typeRef(ENTITY_INTERFACE_TYPE_NAME))
                    .fields(fieldDefinitions)

            // Add interfaces by parent names in the inheritance chain
            var currentEntityDescription: EntityDescription? = entityDescription
            while (currentEntityDescription != null) {
                entityObjectTypeBuilder.withInterface(typeRef(currentEntityDescription.name))
                currentEntityDescription = currentEntityDescription.parentEntityDescription
            }

            // Build the type and add it to the collection
            additionalTypes.add(entityObjectTypeBuilder.build())

            // Create an interface based on the model type
            val entityInterfaceType =
                GraphQLInterfaceType
                    .newInterface()
                    .name(entityDescription.name)
                    .fields(fieldDefinitions)
                    .build()
            // add to interfaces collection
            additionalTypes.add(entityInterfaceType)
            codeRegistryBuilder.typeResolver(entityDescription.name, Helper.TYPE_RESOLVER)

            // Create a collection type (elems, count)
            val entitiesCollectionObjectType =
                GraphQLObjectType
                    .newObject()
                    .name(ENTITIES_COLLECTION_OBJECT_TYPE_PREFIX + entityDescription.name)
                    .field(
                        GraphQLFieldDefinition
                            .newFieldDefinition()
                            .name(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME)
                            .type(nonNull(GraphQLList.list(nonNull(entityInterfaceType)))),
                    ).field(Helper.COUNT_FIELD_DEFINITION)
                    .build()

            // Add the collection type to the list of types
            additionalTypes.add(entitiesCollectionObjectType)
        }
    }
}
