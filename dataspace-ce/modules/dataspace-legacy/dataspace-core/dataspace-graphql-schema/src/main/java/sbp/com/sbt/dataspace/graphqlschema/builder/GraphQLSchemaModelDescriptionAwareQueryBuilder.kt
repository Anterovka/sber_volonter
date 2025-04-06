package sbp.com.sbt.dataspace.graphqlschema.builder

import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription

abstract class GraphQLSchemaModelDescriptionAwareQueryBuilder(
    private val modelDescription: ModelDescription,
) : GraphQLSchemaBaseQueryBuilder() {
    protected fun filteredEntityDescriptions(filter: (EntityDescription) -> Boolean): Sequence<EntityDescription> =
        modelDescription
            .entityDescriptions
            .values
            .asSequence()
            .filterNot { it.name.endsWith("ApiCall") }
            .filter { filter(it) }
}
