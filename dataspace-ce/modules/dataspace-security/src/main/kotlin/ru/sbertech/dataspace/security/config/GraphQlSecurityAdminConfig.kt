package ru.sbertech.dataspace.security.config

import graphql.schema.GraphQLSchema
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import ru.sbertech.dataspace.entitymanager.EntityManagerFactory
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.security.admin.GraphQlSecurityAdminService
import ru.sbertech.dataspace.security.admin.OperationValidator
import ru.sbertech.dataspace.util.ContextHelper
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson
import javax.sql.DataSource

@ConditionalOnProperty(value = ["dataspace.security.graphql.permissions.source"], havingValue = "db")
class GraphQlSecurityAdminConfig {
    @Bean
    fun operationValidator(
        graphQLSchema: GraphQLSchema,
        entitiesReadAccessJson: EntitiesReadAccessJson,
    ): OperationValidator =
        OperationValidator(
            graphQLSchema,
            entitiesReadAccessJson, // TODO Legacy
        )

    @Bean
    fun graphQlSecurityAdminService(
        operationValidator: OperationValidator,
        @Value("\${dataspace.security.gql.permissions.add-as-replace:true}")
        addAsReplace: Boolean,
        contextHelper: ContextHelper,
    ): GraphQlSecurityAdminService =
        GraphQlSecurityAdminService(
            operationValidator,
            addAsReplace,
            contextHelper, // TODO Legacy
        )

    @Bean
    fun contextHelper(
        dataSource: DataSource,
        entityManagerFactory: EntityManagerFactory,
        model: Model,
        entitiesReadAccessJson: EntitiesReadAccessJson,
    ): ContextHelper = ContextHelper(model, entityManagerFactory, dataSource, entitiesReadAccessJson)
}
