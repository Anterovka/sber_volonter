package ru.sbertech.dataspace.util

import com.fasterxml.jackson.databind.ObjectMapper

fun String?.isNotNullOrEmpty() = this != null && this.isNotEmpty()

object DsceUtils {
    val OBJECT_MAPPER: ObjectMapper = ObjectMapper()
}
