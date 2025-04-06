package ru.sbertech.dataspace.entity

import org.springframework.context.ConfigurableApplicationContext

const val REQUEST_NAME: String = "request"
const val RESPONSE_NAME: String = "response"
const val ERROR_NAME: String = "error"

class ContainerInfo(
    var isActive: Boolean = false,
    var context: ConfigurableApplicationContext,
) {
    var requestStat: MutableMap<String, Long> =
        linkedMapOf(
            REQUEST_NAME to 0L,
            RESPONSE_NAME to 0L,
            ERROR_NAME to 0L,
        )
}
