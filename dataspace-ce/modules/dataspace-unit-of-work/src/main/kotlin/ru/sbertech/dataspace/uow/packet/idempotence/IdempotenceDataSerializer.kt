package ru.sbertech.dataspace.uow.packet.idempotence

import ru.sbertech.dataspace.common.forEachSeparated
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.universalvalue.UniversalValueVisitor
import ru.sbertech.dataspace.universalvalue.accept
import java.util.StringJoiner

private const val OPEN_BRACE = '{'

private const val CLOSE_BRACE = '}'

private const val OPEN_SQUARE_BRACE = '['

private const val CLOSE_SQUARE_BRACE = ']'

private const val COMMA = ','

private const val COLON = ':'

//  {h:1, v: [1,2,3,4]},{h:1,v:2},{h:1,v:void},{h:3,v:{id:{k:{w:1,f:6},d:2},created:false}}, {h:1, v: [{id:1, created:true}, {id:2, created: false}]}
internal object IdempotenceDataSerializer {
    fun serialize(idempotenceData: Collection<CommandIdempotenceData>): String {
        val stringJoiner = StringJoiner(COMMA.toString())
        idempotenceData.forEach {
            val stringBuilder = StringBuilder()
            val serializingVisitor = SerializingVisitor(stringBuilder)
            stringBuilder
                .append(OPEN_BRACE)
                .append("h:")
                .append(it.paramsHash)
                .append(COMMA)
                .append("v:")
            it.result.accept(serializingVisitor)
            stringBuilder.append(CLOSE_BRACE)
            stringJoiner.add(stringBuilder)
        }
        return stringJoiner.toString()
    }

    fun deserialize(string: String?): ArrayList<CommandIdempotenceData> {
        if (string == null) {
            return arrayListOf()
        }
        return StringToIdempotenceEntriesParsingParser(string).parse()
    }
}

private class SerializingVisitor(
    private val stringBuilder: StringBuilder,
) : UniversalValueVisitor<Unit> {
    override fun visit(
        object0: Map<String, UniversalValue?>,
        param: Unit,
    ) {
        stringBuilder.append("{")
        object0.entries.forEachSeparated(stringBuilder, ",") { (key, value) ->
            stringBuilder.append(key).append(COLON)
            value?.accept(this) ?: stringBuilder.append("null")
        }
        stringBuilder.append("}")
    }

    override fun visit(
        collection: Collection<UniversalValue?>,
        param: Unit,
    ) {
        stringBuilder.append("[")
        collection.forEachSeparated(stringBuilder, ",") {
            it?.accept(this) ?: stringBuilder.append("null")
        }
        stringBuilder.append("]")
    }

    override fun visit(
        string: String,
        param: Unit,
    ) {
        stringBuilder.append(string)
    }

    override fun visit(
        boolean: Boolean,
        param: Unit,
    ) {
        stringBuilder.append(boolean.toString())
    }
}

private class StringToIdempotenceEntriesParsingParser(
    private val string: String,
) {
    private var position = 0

    fun parse(): ArrayList<CommandIdempotenceData> {
        val commandIdempotenceData = arrayListOf<CommandIdempotenceData>()

        do {
            position = string.indexOf(COLON, position) + 1
            val hash = parseValue() as String
            position = string.indexOf(COLON, position) + 1
            val value = parseValue()
            commandIdempotenceData.add(CommandIdempotenceData(hash, value))
        } while (let {
                position = string.indexOf(OPEN_BRACE, position)
                position
            } != -1
        )
        return commandIdempotenceData
    }

    private fun parseValue(): Any {
        return if (string.elementAt(position) == OPEN_BRACE) {
            ++position
            parseValueToMap()
        } else if (string.elementAt(position) == OPEN_SQUARE_BRACE) {
            ++position
            parseValueToCollection()
        } else {
            val commaIndex = string.indexOf(COMMA, position).takeIf { it != -1 } ?: Int.MAX_VALUE
            val closeSquareBraceIndex = string.indexOf(CLOSE_SQUARE_BRACE, position).takeIf { it != -1 } ?: Int.MAX_VALUE
            val closeBraceIndex = string.indexOf(CLOSE_BRACE, position).takeIf { it != -1 } ?: Int.MAX_VALUE

            var index = commaIndex.coerceAtMost(closeSquareBraceIndex)
            index = index.coerceAtMost(closeBraceIndex)

            val value = string.substring(position, index)
            position = index
            return value
        }
    }

    private fun parseValueToMap(): Any {
        val map = linkedMapOf<String, Any>()

        do {
            val colonIndex = string.indexOf(COLON, position)
            val key = string.substring(position, colonIndex).trim()
            position = colonIndex + 1
            map[key] = parseValue()
        } while (string.elementAt(position++) == COMMA)

        return map
    }

    private fun parseValueToCollection(): Any {
        val collection = arrayListOf<Any>()

        do {
            collection.add(parseValue())
        } while (string.elementAt(position++) == COMMA)

        return collection
    }
}
