package ru.sbertech.dataspace.security.model.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import ru.sbertech.dataspace.security.model.helper.PathConditionDeserializer
import ru.sbertech.dataspace.security.model.helper.PathConditionListSerializer
import java.util.Objects

class Operation {
    var hash: String? = null
    var name: String? = null
    var body: String? = null
    var allowEmptyChecks = false
    var disableJwtVerification = false
    var checkSelects: Set<CheckSelect>? = null

    @JsonDeserialize(using = PathConditionDeserializer::class)
    @JsonSerialize(using = PathConditionListSerializer::class)
    var pathConditions: Map<String, PathCondition>? = null

    constructor()

    constructor(
        hash: String?,
        name: String?,
        body: String?,
        allowEmptyChecks: Boolean,
        disableJwtVerification: Boolean,
        checkSelects: Set<CheckSelect>?,
        pathConditions: Map<String, PathCondition>?,
    ) {
        this.hash = hash
        this.name = name
        this.body = body
        this.allowEmptyChecks = allowEmptyChecks
        this.disableJwtVerification = disableJwtVerification
        this.checkSelects = checkSelects
        this.pathConditions = pathConditions
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val operation = other as Operation
        return (
            allowEmptyChecks == operation.allowEmptyChecks &&
                disableJwtVerification == operation.disableJwtVerification &&
                name == operation.name &&
                hash == operation.hash &&
                body == operation.body &&
                checkSelects == operation.checkSelects &&
                pathConditions == operation.pathConditions
        )
    }

    override fun hashCode(): Int =
        Objects.hash(
            hash,
            name,
            body,
            allowEmptyChecks,
            disableJwtVerification,
            checkSelects,
            pathConditions,
        )
}
