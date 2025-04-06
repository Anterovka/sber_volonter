package ru.sbertech.dataspace.util

import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.servlet.function.ServerRequest

const val MODEL_ID_PATH_VARIABLE = "modelId"

val ServerRequest.modelId: String
    get() = this.pathVariable(MODEL_ID_PATH_VARIABLE)

fun ServerRequest.findModelId() = if (this.pathVariables().containsKey(MODEL_ID_PATH_VARIABLE)) this.modelId else null

@Suppress("LiftReturnOrAssignment")
fun HttpServletRequest.findModelId(): String? {
    // TODO попытаться переделать на pathVariables или хотя бы завязаться на DsceConstants.MODEL_CONTEXT_PATH
    val prefix = "/models/"
    if (!this.servletPath.startsWith(prefix)) {
        return null
    } else {
        val startIndex = prefix.length
        val indexOfSlash = this.servletPath.indexOf("/", startIndex)
        val modelId = if (indexOfSlash < 0) this.servletPath.substring(startIndex) else this.servletPath.substring(startIndex, indexOfSlash)
        return modelId.ifBlank { null }
    }
}
