package ru.sbertech.dataspace.security.userinfo.resolvers

import jakarta.servlet.http.HttpServletRequest
import sbp.sbt.dataspacecore.security.utils.JwtHelper

class JwtKindResolver(
    val headerName: String,
    private val jwtPath: String,
) : KindResolver {
    override fun resolve(resourceRequest: HttpServletRequest): String? {
        val header = resourceRequest.getHeader(headerName) ?: return null
        return JwtHelper.getJwtFieldAsString(header, jwtPath)
    }
}
