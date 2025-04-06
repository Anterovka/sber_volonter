package ru.sbertech.dataspace.security.graphql

import graphql.schema.DataFetchingEnvironment
import ru.sbertech.dataspace.security.model.dto.Operation

interface SecurityRulesFetcher {
    /**
     * Checks if the passed GraphQL query is known to the system. If not, an exception is thrown.
     * Returns the security attached to the request (pre/post checks, predicates of beans on parameters, need for JWT verification).
     *
     * @return list of checks
     */
    fun getSecurityRules(environment: DataFetchingEnvironment): Operation
}
