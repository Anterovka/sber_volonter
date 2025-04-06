package ru.sbertech.dataspace.security

import ru.sbertech.dataspace.security.requestProcessors.IRequestProcessor

/** Используется в случае если присуствуют методы по созданию бина, которые возвращают не один IPortHandler, с сразу несколько
 * через List<IPortHandler>. Т.е. это агрегатор коллекции IPortHandler */
data class SecurityHandlers(
    var handlers: List<IRequestProcessor>,
)
