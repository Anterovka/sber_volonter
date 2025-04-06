package ru.sbertech.dataspace.security.requestProcessors

import jakarta.servlet.http.HttpServletRequest

interface IRequestProcessor : Comparable<IRequestProcessor> {
    /** Возвращает порт на котором работает заданный вид безопаcности  */
    fun isSuitable(req: HttpServletRequest): Boolean

    /** Инициирует контекст безопасности по запросу  */
    fun processRequest(req: HttpServletRequest)

    fun getOrderValue(): Int = Int.MAX_VALUE / 2

    override fun compareTo(other: IRequestProcessor): Int = getOrderValue().compareTo(other.getOrderValue())
}
