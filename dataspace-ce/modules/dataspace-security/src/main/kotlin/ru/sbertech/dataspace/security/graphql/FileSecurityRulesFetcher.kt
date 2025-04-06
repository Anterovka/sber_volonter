package ru.sbertech.dataspace.security.graphql

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.execution.AbortExecutionException
import graphql.schema.DataFetchingEnvironment
import org.apache.commons.io.IOUtils
import org.springframework.util.StringUtils
import ru.sbertech.dataspace.security.model.dto.Operation
import ru.sbertech.dataspace.security.utils.GraphQLHashHelper
import java.io.FileInputStream
import java.io.InputStream
import java.nio.charset.Charset

/** Checks security conditions on requests that come from a file */
class FileSecurityRulesFetcher(
    permissionsString: String,
) : BaseSecurityRulesFetcher() {
    private val operations: Map<String, Operation>

    init {
        try {
            operations =
                objectMapper
                    .readValue(
                        permissionsString,
                        object : TypeReference<List<Operation>>() {},
                    ).associateBy { requireNotEmptyOperationName(it.name) }
                    .apply { values.forEach { it.hash = GraphQLHashHelper.calculateHash(it) } }
                    .toMap()
        } catch (ex: IllegalArgumentException) {
            throw SecurityException("Ошибка десериализации JSON", ex)
        }
    }

    companion object {
        private val objectMapper = ObjectMapper()

        fun fromValue(permissionsString: String): FileSecurityRulesFetcher = FileSecurityRulesFetcher(permissionsString)

        fun fromFile(path: String): FileSecurityRulesFetcher {
            val resourceAsStream: InputStream
            try {
                resourceAsStream = FileInputStream(path)
            } catch (ex: IllegalArgumentException) {
                throw SecurityException("For the passed path, reading the file failed for permissions: $path", ex)
            }
            val data = IOUtils.toByteArray(resourceAsStream)
            return FileSecurityRulesFetcher(String(data, Charset.defaultCharset()))
        }
    }

    override fun getOperationInfo(environment: DataFetchingEnvironment): Operation {
        // We start by noting that requests without a name are immediately sent to the trash.
        val operationName = environment.operationDefinition.name
        if (!StringUtils.hasLength(operationName)) {
            throw AbortExecutionException(
                SecurityException("Security error. Anonymous operations are forbidden."),
            )
        }

        val operation =
            operations[operationName]
                ?: throw AbortExecutionException(
                    SecurityException(
                        "Security error. The operation with the name $operationName is not found in the list of permitted operations",
                    ),
                )
        return operation
    }
}
