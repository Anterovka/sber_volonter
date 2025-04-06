package ru.sbertech.dataspace.security.token

import ru.sbertech.dataspace.security.utils.SecurityKind

class JwtEntity(
    val rawToken: String?,
) : TokenEntity {
    override val headerContent: Map<String, List<String>>
        get() {
            check()
            val headerContent: MutableMap<String, MutableList<String>> = HashMap(1)
            addHeader(headerContent, RequestHeaders.JWT.headerName, createHeaderValue())
            return headerContent
        }

    override val kind: SecurityKind
        get() = SecurityKind.JWT

    private fun check() {
        if (rawToken == null || "" == rawToken) {
            throw SecurityException("Пустое значение JWT")
        }
    }

    private fun createHeaderValue(): String = JWT_PREFIX + rawToken

    companion object {
        const val JWT_PREFIX = "Bearer "
    }
}
