package ru.sbertech.dataspace.uow.packet

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode

object FeatherUtils {
    private val objectMapper = ObjectMapper()
    private val typeReference = object : TypeReference<Map<String, Any>>() {}

    fun jsonToMap(objectNode: ObjectNode): Map<String, Any> = objectMapper.convertValue(objectNode, typeReference)
}
