package ru.sbertech.dataspace.security.userinfo.resolvers

import jakarta.servlet.http.HttpServletRequest
import ru.sbertech.dataspace.security.exception.AuthenticationException
import sbp.sbt.dataspacecore.utils.CommonUtils
import java.util.Base64

class JsonKindResolver(
    val headerName: String,
    private val jsonPath: String,
    private val encodeFlag: Boolean,
) : KindResolver {
    override fun resolve(resourceRequest: HttpServletRequest): String? {
        val header = resourceRequest.getHeader(headerName) ?: return null
        val nodeAtPath = CommonUtils.OBJECT_MAPPER.readTree(header).at("/$jsonPath")
        if (nodeAtPath.isMissingNode) {
            return null
        }
        if (nodeAtPath.isTextual) {
            if (encodeFlag) {
                return String(Base64.getDecoder().decode(nodeAtPath.asText()))
            }
            return nodeAtPath.asText()
        }
        if (encodeFlag) {
            throw AuthenticationException(
                "Не удалось получить информацию о пользователе, так как значение по пути $jsonPath не является строкой.",
            )
        }
        return nodeAtPath.toString()
    }
}
