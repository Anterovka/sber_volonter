package ru.sbertech.dataspace.security.model.helper

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import ru.sbertech.dataspace.security.model.dto.PathCondition

/**
 * В DTO с которым работаем в коде для удобства эти условия хранятся в Map<String, PathCondition>,
 * Сериализуем как список значений.
 */
class PathConditionListSerializer : JsonSerializer<Map<String, PathCondition?>>() {
    override fun serialize(
        value: Map<String, PathCondition?>,
        gen: JsonGenerator?,
        serializers: SerializerProvider?,
    ) {
        gen?.writeObject(value.values)
    }
}
