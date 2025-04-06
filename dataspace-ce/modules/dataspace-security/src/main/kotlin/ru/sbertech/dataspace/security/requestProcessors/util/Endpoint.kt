package ru.sbertech.dataspace.security.requestProcessors.util

import org.springframework.util.AntPathMatcher
import org.springframework.util.PathMatcher
import java.util.Locale

private const val GRAPH_QL_DESCRIPTION = "GraphQL endpoint"

/** Перечень URI, к которым может быть разграничен доступ  */
enum class Endpoint(
    var uri: String,
    val description: String,
) {
    GRAPHQL("/graphql", GRAPH_QL_DESCRIPTION),
    GRAPHQL_SUBSCRIPTION("/subscriptions", GRAPH_QL_DESCRIPTION),
    GRAPHIQL("/graphiql", GRAPH_QL_DESCRIPTION),
    ;

    companion object {
        private val pathMatcher: PathMatcher = AntPathMatcher()

        fun byPath(path: String): Endpoint? {
            val pathInLowerCase = path.lowercase(Locale.getDefault())
            for (value in values()) {
                if (pathMatcher.match(value.uri, pathInLowerCase)) {
                    return value
                }
            }
            return null
        }
    }

    init {
        changeURI(uri)
    }

    /** Изменяет URI элемента. UTI должен выглядеть примерно так: /graphql (в конце слеша не должно быть, вначале слеш)  */
    fun changeURI(uri: String) {
        this.uri = (if (!uri.endsWith("/**")) "$uri/**" else uri).lowercase(Locale.getDefault())
    }
}
