package ru.sbertech.dataspace.modelcontext.configs

import com.sbt.dataspace.pdm.PdmModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import ru.sbertech.dataspace.model.Model
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.EntitiesReadAccessJsonConfiguration
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.EntitiesReadAccessJsonSettings
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.SqlDialect
import sbp.com.sbt.dataspace.feather.modeldescriptionimpl2.ModelDescriptionConfiguration
import sbp.com.sbt.dataspace.feather.modeldescriptionimpl2.ModelDescriptionSettings
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaConfiguration
import javax.sql.DataSource

@ConditionalOnProperty(value = ["dataspace.legacy.enable"], havingValue = "true", matchIfMissing = true)
@Import(
    EntitiesReadAccessJsonConfiguration::class,
    ModelDescriptionConfiguration::class,
    GraphQLSchemaConfiguration::class,
)
class DataspaceLegacyConfiguration {
    @Bean
    fun dataspaceLegacyMutationBuilder(
        dataSource: DataSource,
        model: Model,
        @Value("\${dataspace.uow.isManyAggregatesAllowed:false}") isManyAggregatesAllowed: Boolean,
    ): DataspaceLegacyMutationBuilder = DataspaceLegacyMutationBuilder(dataSource, model, isManyAggregatesAllowed)

    @Bean
    fun modelDescriptionSettings(pdmModel: PdmModel): ModelDescriptionSettings =
        ModelDescriptionSettings()
            .setPdmModel(pdmModel)

    @Bean
    fun entitiesReadAccessJsonSettings(): EntitiesReadAccessJsonSettings =
        EntitiesReadAccessJsonSettings()
            .setSqlDialect(SqlDialect.POSTGRESQL)

    @Bean
    fun namedParameterJdbcTemplate(dataSource: DataSource): NamedParameterJdbcTemplate = NamedParameterJdbcTemplate(dataSource)
}
