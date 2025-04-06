package ru.sbertech.dataspace.security.userinfo

import ru.sbertech.dataspace.security.userinfo.resolvers.KindResolver

/**
 * Часть userInfo в объековом виде
 */
data class UserInfoRule(
    val kind: RuleKind,
    val headerName: String,
    val path: String? = null,
)

/**
 * Представление сырых данных userInfo в объектовом виде (общее правило получения UserInfo)
 */
data class UserInfoSettingsHolder(
    val mandatory: Boolean,
    val rules: List<UserInfoRule> = mutableListOf(),
)

/**
 * Содержит информаицю об обязательности наличия информации о пользователе и способы ее получения
 * Применяется, если для заданного вида безопасности не были указаны (перекрыты) источники.
 */
data class UserInfoDefaultSettingsHolder(
    val mandatory: Boolean,
    val resolvers: List<KindResolver>,
)
