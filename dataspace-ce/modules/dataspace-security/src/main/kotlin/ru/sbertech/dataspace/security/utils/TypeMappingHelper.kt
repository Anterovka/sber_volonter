package ru.sbertech.dataspace.security.utils

import sbp.com.sbt.dataspace.feather.modeldescription.DataType

class TypeMappingHelper {
    companion object {
        val TYPES_MAPPING = HashMap<String, DataType>()

        init {
            TYPES_MAPPING["character"] = DataType.CHARACTER
            TYPES_MAPPING["string"] = DataType.STRING
            TYPES_MAPPING["byte"] = DataType.BYTE
            TYPES_MAPPING["short"] = DataType.SHORT
            TYPES_MAPPING["integer"] = DataType.INTEGER
            TYPES_MAPPING["long"] = DataType.LONG
            TYPES_MAPPING["float"] = DataType.FLOAT
            TYPES_MAPPING["double"] = DataType.DOUBLE
            TYPES_MAPPING["bigdecimal"] = DataType.BIG_DECIMAL
            TYPES_MAPPING["localdate"] = DataType.DATE
            TYPES_MAPPING["localdatetime"] = DataType.DATETIME
            TYPES_MAPPING["offsetdatetime"] = DataType.OFFSET_DATETIME
            TYPES_MAPPING["time"] = DataType.TIME
            TYPES_MAPPING["date"] = DataType.DATETIME
            TYPES_MAPPING["boolean"] = DataType.BOOLEAN
            TYPES_MAPPING["byte[]"] = DataType.BYTE_ARRAY
        }
    }
}
