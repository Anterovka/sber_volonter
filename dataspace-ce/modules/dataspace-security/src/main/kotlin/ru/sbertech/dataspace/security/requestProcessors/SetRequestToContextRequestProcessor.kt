package ru.sbertech.dataspace.security.requestProcessors

import jakarta.servlet.http.HttpServletRequest
import sbp.sbt.dataspacecore.security.utils.SecurityUtils

/** Заносит запрос в ThreadLocal контекст */
class SetRequestToContextRequestProcessor : IRequestProcessor {
    override fun isSuitable(req: HttpServletRequest): Boolean = true

    override fun processRequest(request: HttpServletRequest) {
        val token = SecurityUtils.getOrCreateCurrentToken()
        token.request = request
    }
}
