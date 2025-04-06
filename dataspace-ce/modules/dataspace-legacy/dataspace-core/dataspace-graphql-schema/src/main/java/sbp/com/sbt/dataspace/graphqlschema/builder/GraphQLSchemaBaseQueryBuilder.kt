package sbp.com.sbt.dataspace.graphqlschema.builder

import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLTypeReference
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription
import sbp.com.sbt.dataspace.graphqlschema.CalcExprFieldsPlacement
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaSettings
import sbp.com.sbt.dataspace.graphqlschema.Helper
import sbp.com.sbt.dataspace.graphqlschema.datafetcher.ExtendedPropertyDataFetcher

abstract class GraphQLSchemaBaseQueryBuilder : GraphQLSchemaQueryBuilder {
    companion object {
        val extendedPropertyDataFetcher = ExtendedPropertyDataFetcher()
    }

    protected fun addExtendedPropertyDataFetchers(
        entityDescription: EntityDescription,
        typeName: String,
        codeRegistryBuilder: GraphQLCodeRegistry.Builder,
        fieldDefinitions: ArrayList<GraphQLFieldDefinition>,
    ) {
        fieldDefinitions
//                .filter {
//                    !entityDescription.primitiveDescriptions.values
//                            .any { primitiveDescription -> primitiveDescription.name == it.name }
//                }
            .forEach {
                codeRegistryBuilder.dataFetcher(FieldCoordinates.coordinates(typeName, it.name), extendedPropertyDataFetcher)
            }
    }

    protected fun addCalcFields(
        settings: GraphQLSchemaSettings,
        fieldDefinitions: ArrayList<GraphQLFieldDefinition>,
    ) {
        when (settings.calcExprFieldsPlacement) {
            CalcExprFieldsPlacement.ON_EACH_TYPE -> {
                // _getString, _getDouble и т.п.
                Helper.TYPE_MAPPING.values
                    .filterIsInstance<GraphQLScalarType>()
                    .associateBy { getTypeName(it.name.removePrefix("_")) }
                    .forEach {
                        fieldDefinitions.add(
                            GraphQLFieldDefinition
                                .newFieldDefinition()
                                .name(GraphQLSchemaHelper.GET_PREFIX + it.key)
                                .argument(Helper.EXPRESSION_ARGUMENT)
                                .type(it.value)
                                .build(),
                        )
                    }
            }
            CalcExprFieldsPlacement.ON_SEPARATE_TYPE -> {
                fieldDefinitions +=
                    GraphQLFieldDefinition
                        .newFieldDefinition()
                        .name(GraphQLSchemaHelper.CALC_FIELD_NAME)
                        .type(GraphQLNonNull.nonNull(GraphQLTypeReference.typeRef(GraphQLSchemaHelper.CALCULATION_OBJECT_TYPE_NAME)))
                        .build()
            }
            else -> { /* Unexpected */ }
        }
    }

    private fun getTypeName(name: String): String =
        when (name) {
            "Float" -> "Double"
            "Float4" -> "Float"
            else -> name
        }
}
