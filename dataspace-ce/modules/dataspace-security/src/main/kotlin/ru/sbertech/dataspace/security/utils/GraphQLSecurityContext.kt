package ru.sbertech.dataspace.security.utils

import ru.sbertech.dataspace.security.model.dto.Operation

// TODO: Put this into the DataFetchingEnvironment?
data class GraphQLSecurityContext(
    val allVariables: Map<String, Any> = emptyMap(),
    /** Information about necessary checks within the security of the operation */
    val secureOperation: Operation? = null,
)
