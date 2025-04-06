package ru.sbertech.dataspace.model.pdm

import com.sbt.mg.data.model.XmlModelClassProperty
import ru.sbertech.dataspace.primitive.type.PrimitiveType

private val primitiveTypeByPdmName: Map<String, PrimitiveType> =
    linkedMapOf(
        "Character" to PrimitiveType.Char,
        "String" to PrimitiveType.String,
        "Byte" to PrimitiveType.Byte,
        "Short" to PrimitiveType.Short,
        "Integer" to PrimitiveType.Int,
        "Long" to PrimitiveType.Long,
        "Float" to PrimitiveType.Float,
        "Double" to PrimitiveType.Double,
        "BigDecimal" to PrimitiveType.BigDecimal,
        "LocalDate" to PrimitiveType.LocalDate,
        "Date" to PrimitiveType.LocalDateTime,
        "LocalDateTime" to PrimitiveType.LocalDateTime,
        "OffsetDateTime" to PrimitiveType.OffsetDateTime,
        "Boolean" to PrimitiveType.Boolean,
        "byte[]" to PrimitiveType.ByteArray,
    )

internal fun primitiveType(xmlModelClassProperty: XmlModelClassProperty): PrimitiveType? =
    primitiveTypeByPdmName[xmlModelClassProperty.type].let {
        if (it == PrimitiveType.String && xmlModelClassProperty.length > 4000) PrimitiveType.Text else it
    }
