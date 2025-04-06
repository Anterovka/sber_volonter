package ru.sbertech.dataspace.security.graphql

import graphql.execution.AbortExecutionException
import graphql.schema.DataFetchingEnvironment
import ru.sbertech.dataspace.security.exception.AuthenticationException
import ru.sbertech.dataspace.security.exception.SecurityConfigException
import ru.sbertech.dataspace.security.model.dto.Operation
import ru.sbertech.dataspace.security.utils.GraphQLHashHelper

abstract class BaseSecurityRulesFetcher : SecurityRulesFetcher {
    protected abstract fun getOperationInfo(environment: DataFetchingEnvironment): Operation

    override fun getSecurityRules(environment: DataFetchingEnvironment): Operation {
        val operation = getOperationInfo(environment)

        // We calculate the hash sum of the mutation, compare
        val hash = GraphQLHashHelper.calculateHash(environment.document)

        if (!operation.hash.equals(hash)) {
            // The name matches, but the hash does not.
            throw AbortExecutionException(
                AuthenticationException("Access is denied. The current and saved operation hash do not match."),
            )
        }

        // TODO test
        if (!operation.allowEmptyChecks && operation.checkSelects.isNullOrEmpty()) {
            throw AbortExecutionException(
                SecurityConfigException(
                    "Configuration error of access delimitation. For GraphQL request, no checks for CheckSelects are set." +
                        " It is necessary either to add a condition check or set the allowEmptyChecks flag to 'true'.",
                ),
            )
        }

        return operation
    }

    fun requireNotEmptyOperationName(name: String?) =
        if (!name.isNullOrEmpty()) {
            name
        } else {
            throw AuthenticationException("Security Error. Anonymous transactions are prohibited")
        }
}
