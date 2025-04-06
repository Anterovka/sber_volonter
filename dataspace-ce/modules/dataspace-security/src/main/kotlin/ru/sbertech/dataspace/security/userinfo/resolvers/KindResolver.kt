package ru.sbertech.dataspace.security.userinfo.resolvers

import jakarta.servlet.http.HttpServletRequest

/**
 * Интерфейс для различных способов получения информации о пользователе (Jwt, Json, RawHeader)
 */
interface KindResolver {
    fun resolve(resourceRequest: HttpServletRequest): String?
}
