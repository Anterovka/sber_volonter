package sbp.com.sbt.dataspace.graphqlschema

import graphql.language.Field
import ru.sbertech.dataspace.security.model.dto.PathCondition
import sbp.com.sbt.dataspace.feather.modeldescription.ObjectWithMetaDataManager
import java.util.Deque
import java.util.LinkedList

/** Used when processing a GQL request.
 * Stores information about the current processed type (field), metadata about the type from feather and a link to the higher-level type (field) within the GQL request */
data class DataFetcherStep(
    val field: Field,
    /** Fizerovsky Description of the field - can be EntityDescription, PropertyDescription, ReferenceDescription, etc.*/
    val fieldDescription: ObjectWithMetaDataManager?,
    val parent: DataFetcherStep?,
) {
    /**
     * Counts the number of occurrences of the specified nodes in the current path
     */
    fun countNodes(nodes: Set<String>): Int {
        tailrec fun rec(
            count: Int,
            step: DataFetcherStep,
        ): Int {
            val newCount = if (step.field.name in nodes) count + 1 else count
            return if (step.parent == null) newCount else rec(newCount, step.parent)
        }

        return rec(0, this)
    }

    /**
     * Current step path
     */
    fun getPath(): String {
        tailrec fun rec(
            parts: Deque<String>,
            step: DataFetcherStep,
        ): String {
            parts.push(PathCondition.getPathPart(step.field))
            return if (step.parent == null) parts.joinToString(".") else rec(parts, step.parent)
        }

        return rec(LinkedList(), this)
    }
}
