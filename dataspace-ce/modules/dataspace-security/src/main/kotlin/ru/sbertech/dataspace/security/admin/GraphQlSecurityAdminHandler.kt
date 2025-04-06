package ru.sbertech.dataspace.security.admin

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.RouterFunctions
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.paramOrNull
import ru.sbertech.dataspace.security.exception.AdminException
import ru.sbertech.dataspace.security.model.dto.Operation
import ru.sbertech.dataspace.security.utils.SecurityConstants
import ru.sbertech.dataspace.services.exception.ContextOperationException
import ru.sbertech.dataspace.util.ModelResolver

class GraphQlSecurityAdminHandler(
    private val modelResolver: ModelResolver,
) {
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(GraphQlSecurityAdminHandler::class.java)

        private const val OPERATION_NAME_QUERY_VARIABLE = "name"
        private const val OPERATION_NAME_PATH_VARIABLE = "operationName"
        private const val PAGE_QUERY_VARIABLE = "page"
        private const val PAGE_SIZE_QUERY_VARIABLE = "pageSize"

        private const val PERMISSIONS_BASE_ROUTE = "${SecurityConstants.SECURITY_BASE_ROUTE}/permissions"
        private const val REPLACE_OPERATION = "$PERMISSIONS_BASE_ROUTE/operations/{operationName}"
        private const val DELETE_OPERATION = "$PERMISSIONS_BASE_ROUTE/operations/{operationName}"
        private const val SEARCH_OPERATIONS = "$PERMISSIONS_BASE_ROUTE/operations"
        private const val CREATE_OPERATION = "$PERMISSIONS_BASE_ROUTE/operations"
        private const val CREATE_OPERATIONS = "$PERMISSIONS_BASE_ROUTE/operations-bulk/create"
        private const val OPERATIONS_MERGE = "$PERMISSIONS_BASE_ROUTE/operations-bulk/merge"
        private const val DELETE_OPERATIONS = "$PERMISSIONS_BASE_ROUTE/operations-bulk/deleteAll"
        private const val REPLACE_OPERATIONS = "$PERMISSIONS_BASE_ROUTE/operations-bulk/replace"
        private const val REPLACE_ALL_OPERATIONS = "$PERMISSIONS_BASE_ROUTE/operations-bulk/replaceAll"

        fun initialize(modelResolver: ModelResolver): RouterFunction<ServerResponse> {
            val handler = GraphQlSecurityAdminHandler(modelResolver)
            val builder = RouterFunctions.route()
            builder
                .GET(SEARCH_OPERATIONS, handler::searchOperations)
                .POST(CREATE_OPERATION, handler::createOperation)
                .POST(CREATE_OPERATIONS, handler::createOperations)
                .POST(OPERATIONS_MERGE, handler::mergeOperations)
                .POST(REPLACE_OPERATIONS, handler::replaceOperations)
                .POST(REPLACE_ALL_OPERATIONS, handler::replaceAllOperations)
                .DELETE(DELETE_OPERATION, handler::deleteOperation)
                .DELETE(DELETE_OPERATIONS, handler::deleteOperations)
                .PUT(REPLACE_OPERATION, handler::replaceOperation)
                .onError(Throwable::class.java) { exception, _ -> handleError(exception) }
            return builder.build()
        }

        private fun handleError(exception: Throwable): ServerResponse {
            LOGGER.error("GraphQlSecurityAdminService exception", exception)

            return when (exception) {
                is AdminException -> {
                    ServerResponse
                        .badRequest()
                        .body(exception.message ?: "An error occurred")
                }
                else -> {
                    ServerResponse
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(exception.message ?: "Internal server error")
                }
            }
        }
    }

    /**
     * Получить сервис администрирования, либо исключение, если его нет (не включён) в контексте указанной модели
     */
    private fun getAdminService(request: ServerRequest): GraphQlSecurityAdminService {
        try {
            return modelResolver
                .resolveActiveContext(request)
                .getBean(GraphQlSecurityAdminService::class.java)
        } catch (exception: ContextOperationException) {
            with("Could not resolve context") {
                LOGGER.error(this, exception)
                throw AdminException(this, exception)
            }
        } catch (exception: NoSuchBeanDefinitionException) {
            with("Could not resolve graphql admin service beans, probably admin service is disabled in the specified model") {
                LOGGER.error(this, exception)
                throw AdminException(this, exception)
            }
        }
    }

    fun searchOperations(request: ServerRequest): ServerResponse =
        ServerResponse.ok().body(
            with(getAdminService(request)) {
                searchOperations(
                    request.paramOrNull(OPERATION_NAME_QUERY_VARIABLE),
                    request.paramOrNull(PAGE_QUERY_VARIABLE)?.toInt(),
                    request.paramOrNull(PAGE_SIZE_QUERY_VARIABLE)?.toInt(),
                )
            },
        )

    fun createOperation(request: ServerRequest): ServerResponse {
        with(getAdminService(request)) {
            createOperation(request.body(Operation::class.java))
        }

        return ServerResponse.ok().build()
    }

    fun createOperations(request: ServerRequest): ServerResponse {
        with(getAdminService(request)) {
            createOperations(request.body(object : ParameterizedTypeReference<List<Operation>>() {}))
        }

        return ServerResponse.ok().build()
    }

    fun mergeOperations(request: ServerRequest): ServerResponse {
        with(getAdminService(request)) {
            mergeOperations(request.body(object : ParameterizedTypeReference<List<Operation>>() {}))
        }

        return ServerResponse.ok().build()
    }

    fun replaceOperations(request: ServerRequest): ServerResponse {
        with(getAdminService(request)) {
            replaceOperations(request.body(object : ParameterizedTypeReference<List<Operation>>() {}))
        }

        return ServerResponse.ok().build()
    }

    fun replaceAllOperations(request: ServerRequest): ServerResponse {
        with(getAdminService(request)) {
            replaceAllOperations(request.body(object : ParameterizedTypeReference<List<Operation>>() {}))
        }

        return ServerResponse.ok().build()
    }

    fun replaceOperation(request: ServerRequest): ServerResponse {
        with(getAdminService(request)) {
            // Get the pathVariable
            val operationName =
                try {
                    request.pathVariable(OPERATION_NAME_PATH_VARIABLE)
                } catch (exception: IllegalArgumentException) {
                    throw AdminException("Operation name path variable is absent", exception)
                }
            // Get the operation
            val operation =
                request.body(Operation::class.java).apply {
                    if (name != null && name != operationName) {
                        throw AdminException("Operation name does not match the name from uri.")
                    }
                    name = name ?: operationName
                }
            // Replace
            replaceOperation(operation)
        }

        return ServerResponse.ok().build()
    }

    fun deleteOperation(request: ServerRequest): ServerResponse {
        with(getAdminService(request)) {
            val operationName =
                try {
                    request.pathVariable(OPERATION_NAME_PATH_VARIABLE)
                } catch (exception: IllegalArgumentException) {
                    throw AdminException("Operation name path variable is absent", exception)
                }
            deleteOperation(operationName)
        }

        return ServerResponse.ok().build()
    }

    fun deleteOperations(request: ServerRequest): ServerResponse {
        with(getAdminService(request)) {
            deleteOperations()
        }

        return ServerResponse.ok().build()
    }
}
