package ru.sbertech.dataspace.helpers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.universalvalue.UniversalValueVisitor
import ru.sbertech.dataspace.universalvalue.accept

class JsonNodeReturningVisitor(
    private val objectMapper: ObjectMapper,
) : UniversalValueVisitor<JsonNode> {
    override fun visit(
        object0: Map<String, UniversalValue?>,
        param: Unit,
    ) = objectMapper.createObjectNode().apply {
        setAll<JsonNode>(object0.mapValues { (_, value) -> value?.accept(this@JsonNodeReturningVisitor) ?: objectMapper.nullNode() })
    }

    override fun visit(
        collection: Collection<UniversalValue?>,
        param: Unit,
    ) = objectMapper.createArrayNode().apply {
        addAll(collection.map { it?.accept(this@JsonNodeReturningVisitor) ?: objectMapper.nullNode() }.sortedBy { it.textValue() })
    }

    override fun visit(
        string: String,
        param: Unit,
    ) = objectMapper.nodeFactory.textNode(string)
}
