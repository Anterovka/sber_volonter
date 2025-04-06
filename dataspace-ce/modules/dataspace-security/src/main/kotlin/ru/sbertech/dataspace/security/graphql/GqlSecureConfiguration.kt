package ru.sbertech.dataspace.security.graphql

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson
import java.nio.file.FileSystems

class GqlSecureConfiguration {
    @Bean
    @ConditionalOnProperty(value = ["dataspace.security.graphql.permissions.source"], havingValue = "file")
    fun fileSecurityRulesFetcher(
        @Value("\${child.model.path:model-path}") modelPath: String,
    ): SecurityRulesFetcher =
        FileSecurityRulesFetcher.fromFile(
            modelPath + FileSystems.getDefault().separator + PERMISSIONS_PATH,
        )

    @Bean
    @ConditionalOnProperty(value = ["dataspace.security.graphql.permissions.source"], havingValue = "db")
    fun databaseSecurityRulesFetcher(entitiesReadAccessJson: EntitiesReadAccessJson): SecurityRulesFetcher =
        DatabaseSecurityRulesFetcher(entitiesReadAccessJson)

    @Bean
    @ConditionalOnExpression(
        "'\${dataspace.security.graphql.permissions.source:}' == 'db'" +
            "  || '\${dataspace.security.graphql.permissions.source:}' == 'file'",
    )
    fun gqlSecurityFlagSetterInterceptor() = GqlSecurityFlagSetterInterceptor()

    @Bean
    fun gqlRequestSetterInterceptor() = GqlRequestSetterInterceptor()

    companion object {
        const val PERMISSIONS_PATH = "graphql-permissions.json"
    }
}
