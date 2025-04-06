package sbp.com.sbt.dataspace.graphqlschema

import graphql.language.Field
import graphql.language.FragmentDefinition
import graphql.schema.DataFetchingEnvironment
import ru.sbertech.dataspace.security.utils.GraphQLSecurityContext
import sbp.com.sbt.dataspace.feather.modeldescription.ObjectWithMetaDataManager

/**
 * Aggregates DataFetchingEnvironment, GraphQLSecurityContext, and DataFetcherStep to reduce the number of parameters.
 * propagated to methods.
The text "securityContext.allVariables" differs from "DataFetchingEnvironment.variables" in that the former contains all variables (received).
 * variables (including those that are not defined by GQL query arguments), while in the second one only those variable
 * which are explicitly used in GQL (defined in query arguments).
 * This approach was used when there was no variable forwarding through annotations yet.
 */
class DataFetcherContext(
    val dataFetchingEnvironment: DataFetchingEnvironment,
    val securityContext: GraphQLSecurityContext?,
    // the current processed field of GQL with metadata about it
    // TODO possibly can be and notNull
    var dataFetcherStep: DataFetcherStep?,
) {
    var level: Int = 0
        private set

    val parsedVariables: Map<String, Any>
        get() = dataFetchingEnvironment.variables

    val fragments: Map<String, FragmentDefinition>
        get() = dataFetchingEnvironment.fragmentsByName

    /**
     * @param fieldDescription metadata about the type from feather
     */
    fun addStep(
        field: Field,
        fieldDescription: ObjectWithMetaDataManager?,
    ) {
        val step = DataFetcherStep(field, fieldDescription, dataFetcherStep)
        dataFetcherStep = step
        level++
    }

    fun removeStep(): DataFetcherStep? {
        if (dataFetcherStep == null) {
            return null
        }
        val step = dataFetcherStep
        dataFetcherStep = dataFetcherStep!!.parent
        level--
        return step
    }
}
