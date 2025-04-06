package ru.sbertech.dataspace.security.model.dto

import graphql.language.Field
import java.util.Objects

class PathCondition {
    var path: String? = null
    var cond: String? = null

    companion object {
        fun getPathPart(field: Field): String = if (field.alias != null) field.alias else field.name

        fun asMap(pathConditions: Collection<PathCondition>): Map<String, PathCondition> =
            pathConditions.associateBy {
                requireNotNull(it.path) { "PathCondition's path cannot be null" }
            }
    }

    constructor()

    constructor(path: String?, cond: String?) {
        this.path = path
        this.cond = cond
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as PathCondition
        return path == that.path && cond == that.cond
    }

    override fun hashCode(): Int = Objects.hash(path, cond)
}
