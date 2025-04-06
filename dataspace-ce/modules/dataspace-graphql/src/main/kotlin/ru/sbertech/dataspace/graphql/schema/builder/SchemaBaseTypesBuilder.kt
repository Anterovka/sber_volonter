package ru.sbertech.dataspace.graphql.schema.builder

import graphql.schema.FieldCoordinates.coordinates
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLInterfaceType.newInterface
import graphql.schema.GraphQLList.list
import graphql.schema.GraphQLNonNull.nonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import graphql.schema.GraphQLOutputType
import graphql.schema.GraphQLTypeReference.typeRef
import ru.sbertech.dataspace.graphql.schema.datafetcher.FieldsByAliasDataFetcher
import ru.sbertech.dataspace.graphql.schema.datafetcher.SearchFieldsDataFetcher
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.defaultSearchSpecificationArguments
import ru.sbertech.dataspace.graphql.schema.utils.ScalarTypes
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.CALCULATION_OBJECT_TYPE_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.CALC_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.ELEMENTS_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.ENTITIES_COLLECTION_OBJECT_TYPE_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.ENTITY_INTERFACE_TYPE_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.ENTITY_OBJECT_TYPE_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.GROUP_OBJECT_TYPE_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.QUERY_OBJECT_TYPE_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.SEARCH_FIELD_NAME_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.countFieldDefinition
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.getType
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.mandatoryIdFieldDefinition
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.typeResolver
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.EnumCollectionProperty
import ru.sbertech.dataspace.model.property.EnumProperty
import ru.sbertech.dataspace.model.property.PrimitiveCollectionProperty
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.property.PropertyParameterizedVisitor
import ru.sbertech.dataspace.model.property.ReferenceProperty
import ru.sbertech.dataspace.model.type.EmbeddableType
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.model.type.EnumType
import ru.sbertech.dataspace.model.type.TypeParameterizedVisitor

class SchemaBaseTypesBuilder(
    private val searchFieldsDataFetcher: SearchFieldsDataFetcher,
    private val scalarTypes: ScalarTypes,
) : TypeParameterizedVisitor<SchemaBuildingData, Unit>,
    PropertyParameterizedVisitor<ArrayList<GraphQLFieldDefinition>, Unit> {
    override fun visit(
        enumType: EnumType,
        param: SchemaBuildingData,
    ) {
        // TODO
    }

    override fun visit(
        embeddableType: EmbeddableType,
        param: SchemaBuildingData,
    ) {
        val embeddableObjectTypeBuilder = newObject().name(GROUP_OBJECT_TYPE_PREFIX + embeddableType.name)

        val fieldDefinitions = ArrayList<GraphQLFieldDefinition>()
        embeddableType.properties.forEach {
            it.accept(this, fieldDefinitions)
        }

        embeddableObjectTypeBuilder.fields(fieldDefinitions)
        param.additionalTypes.add(embeddableObjectTypeBuilder.build())
    }

    override fun visit(
        entityType: EntityType,
        param: SchemaBuildingData,
    ) {
        val fieldDefinitions = ArrayList<GraphQLFieldDefinition>()
        fieldDefinitions.add(mandatoryIdFieldDefinition)

        entityType.inheritedPersistableProperties
            .filter { !it.isId }
            .forEach {
                it.accept(this, fieldDefinitions)
            }

        fieldDefinitions.add(
            newFieldDefinition()
                .name(CALC_FIELD_NAME)
                .type(nonNull(typeRef(CALCULATION_OBJECT_TYPE_NAME)))
                .build(),
        )

        val typeName = ENTITY_OBJECT_TYPE_PREFIX + entityType.name

        val entityObjectTypeBuilder: GraphQLObjectType.Builder =
            newObject()
                .name(typeName)
                .withInterface(typeRef(ENTITY_INTERFACE_TYPE_NAME))
                .fields(fieldDefinitions)

        var currentEntityType: EntityType? = entityType
        while (currentEntityType != null) {
            entityObjectTypeBuilder.withInterface(typeRef(currentEntityType.name))
            currentEntityType = currentEntityType.parentEntityType
        }

        param.additionalTypes.add(entityObjectTypeBuilder.build())

        val entityInterfaceType =
            newInterface()
                .name(entityType.name)
                .fields(fieldDefinitions)
                .build()

        param.additionalTypes.add(entityInterfaceType)
        param.codeRegistryBuilder.typeResolver(entityType.name, typeResolver)

        val entitiesCollectionObjectType =
            newObject()
                .name(ENTITIES_COLLECTION_OBJECT_TYPE_PREFIX + entityType.name)
                .field(
                    newFieldDefinition()
                        .name(ELEMENTS_FIELD_NAME)
                        .type(nonNull(list(nonNull(entityInterfaceType)))),
                ).field(countFieldDefinition)
                .build()

        param.additionalTypes.add(entitiesCollectionObjectType)

        val searchFieldName = "$SEARCH_FIELD_NAME_PREFIX${entityType.name}"
        param.queryTypeBuilder.field(
            newFieldDefinition()
                .name(searchFieldName)
                .arguments(defaultSearchSpecificationArguments)
                .type(nonNull(entitiesCollectionObjectType)),
        )

        param.codeRegistryBuilder.dataFetcher(
            coordinates(QUERY_OBJECT_TYPE_NAME, searchFieldName),
            searchFieldsDataFetcher,
        )
        fieldDefinitions.forEach {
            param.codeRegistryBuilder.dataFetcher(coordinates(typeName, it.name), FieldsByAliasDataFetcher)
        }
    }

    override fun visit(
        primitiveProperty: PrimitiveProperty,
        param: ArrayList<GraphQLFieldDefinition>,
    ) {
        param.add(
            newFieldDefinition()
                .name(primitiveProperty.name)
                .type(getType<GraphQLOutputType>(scalarTypes.typeMapping[primitiveProperty.type], primitiveProperty.isOptional))
                .build(),
        )
    }

    override fun visit(
        enumProperty: EnumProperty,
        param: ArrayList<GraphQLFieldDefinition>,
    ) {
        // TODO
    }

    override fun visit(
        primitiveCollectionProperty: PrimitiveCollectionProperty,
        param: ArrayList<GraphQLFieldDefinition>,
    ) {
        param.add(
            newFieldDefinition()
                .name(primitiveCollectionProperty.name)
                .arguments(defaultSearchSpecificationArguments)
                .type(scalarTypes.collectionTypeMapping[primitiveCollectionProperty.type])
                .build(),
        )
    }

    override fun visit(
        enumCollectionProperty: EnumCollectionProperty,
        param: ArrayList<GraphQLFieldDefinition>,
    ) {
        // TODO
    }

    override fun visit(
        embeddedProperty: EmbeddedProperty,
        param: ArrayList<GraphQLFieldDefinition>,
    ) {
        param.add(
            newFieldDefinition()
                .name(embeddedProperty.name)
                .type(typeRef(GROUP_OBJECT_TYPE_PREFIX + embeddedProperty.type.name))
                .build(),
        )
    }

    override fun visit(
        referenceProperty: ReferenceProperty,
        param: ArrayList<GraphQLFieldDefinition>,
    ) {
        // TODO
    }
}
