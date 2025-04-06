package ru.sbertech.dataspace.security.userinfo.resolvers

import jakarta.servlet.http.HttpServletRequest
import java.util.Base64

class RawHeaderKindResolver(
    val path: String,
    val encodeFlag: Boolean,
) : KindResolver {
    override fun resolve(resourceRequest: HttpServletRequest): String? {
        val headerValue = resourceRequest.getHeader(path)
        if (encodeFlag && headerValue != null) {
            // декодировать из base-64
            return String(Base64.getDecoder().decode(headerValue))
        }
        return headerValue
    }
}
