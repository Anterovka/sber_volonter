package ru.sbertech.dataspace.security.userinfo

import com.fasterxml.jackson.core.JsonProcessingException
import jakarta.servlet.http.HttpServletRequest
import ru.sbertech.dataspace.security.exception.AuthenticationException
import ru.sbertech.dataspace.security.userinfo.RuleKind.Companion.findByValue
import ru.sbertech.dataspace.security.userinfo.resolvers.JsonKindResolver
import ru.sbertech.dataspace.security.userinfo.resolvers.JwtKindResolver
import ru.sbertech.dataspace.security.userinfo.resolvers.KindResolver
import ru.sbertech.dataspace.security.userinfo.resolvers.RawHeaderKindResolver
import sbp.sbt.dataspacecore.utils.CommonUtils
import java.util.Collections

object UserInfoHelper {
    /** userInfo to UserInfoSettingsHolder deserializer */
    @JvmStatic
    fun transform(userInfoAsJson: String): UserInfoSettingsHolder {
        val userInfoAsJsonNode =
            try {
                CommonUtils.OBJECT_MAPPER.readTree(userInfoAsJson)
            } catch (e: JsonProcessingException) {
                throw IllegalArgumentException("Неправильный формат свойства, описывающего идентификацию пользователю", e)
            }

        val mandatoryNode = userInfoAsJsonNode["mandatory"]
        requireNotNull(mandatoryNode) { "Поле mandatory должно быть заполнено" }
        val mandatory = mandatoryNode.asBoolean()

        val rulesNode = userInfoAsJsonNode["rules"]
        if (mandatory) {
            require(rulesNode.isArray) { "Поле rules должно быть коллекцией и содержать хотя бы один элемент" }
        }
        val parsedRules: MutableList<UserInfoRule> = ArrayList()
        for (jsonNode in rulesNode) {
            val kind = jsonNode["kind"].asText()
            val ruleKind =
                findByValue(kind)
                    ?: throw IllegalArgumentException("Поле rule имеет неверное значение")
            val item = jsonNode["item"].asText()
            if (ruleKind === RuleKind.RAW_HEADER || ruleKind === RuleKind.BASE_64_ENCODED_RAW_HEADER) {
                parsedRules.add(UserInfoRule(ruleKind, item, null))
            } else {
                val delimiterIndex = item.indexOf("/")
                require(delimiterIndex != -1) { "Поле item должно иметь разделитель /" }
                val headerName = item.substring(0, delimiterIndex)
                val address = item.substring(delimiterIndex + 1)
                parsedRules.add(UserInfoRule(ruleKind, headerName, address))
            }
        }
        return UserInfoSettingsHolder(mandatory, parsedRules)
    }

    @JvmStatic
    fun createResolvers(userInfoSettingsHolder: UserInfoSettingsHolder): List<KindResolver> {
        val tempKindResolvers = mutableListOf<KindResolver>()
        for ((kind, headerName, path) in userInfoSettingsHolder.rules) {
            when (kind) {
                RuleKind.JWT -> {
                    tempKindResolvers.add(JwtKindResolver(headerName, path!!.replace('.', '/')))
                }
                RuleKind.JSON -> {
                    tempKindResolvers.add(JsonKindResolver(headerName, path!!.replace('.', '/'), false))
                }
                RuleKind.BASE_64_ENCODED_JSON -> {
                    tempKindResolvers.add(JsonKindResolver(headerName, path!!.replace('.', '/'), true))
                }
                RuleKind.RAW_HEADER -> {
                    tempKindResolvers.add(RawHeaderKindResolver(headerName, false))
                }
                RuleKind.BASE_64_ENCODED_RAW_HEADER -> {
                    tempKindResolvers.add(RawHeaderKindResolver(headerName, true))
                }
            }
        }
        return Collections.unmodifiableList(tempKindResolvers)
    }

    @JvmStatic
    fun getUserInfo(
        request: HttpServletRequest,
        isUserInfoMandatory: Boolean,
        resolvers: List<KindResolver>?,
    ): String? {
        if (!resolvers.isNullOrEmpty()) {
            val resolved = getStringValue(request, resolvers)
            if (isUserInfoMandatory && resolved.isNullOrEmpty()) {
                throw AuthenticationException("Не найдена обязательная информация о пользователе")
            }
            if (!resolved.isNullOrEmpty() && resolved.length > 254) {
                throw AuthenticationException("Информация о пользователе превышает 254 символа")
            }
            return resolved
        }
        return null
    }

    private fun getStringValue(
        request: HttpServletRequest,
        resolvers: List<KindResolver>,
    ): String? {
        for (kindResolver in resolvers) {
            val resolved = kindResolver.resolve(request)
            if (!resolved.isNullOrEmpty()) {
                return resolved
            }
        }
        return null
    }

    @JvmStatic
    fun pickUserInfo(
        userInfoAsJson: String,
        defaultSettingsHolder: UserInfoDefaultSettingsHolder?,
    ): Pair<Boolean?, List<KindResolver>?> =
        if (userInfoAsJson.isNotEmpty()) {
            val userInfoSettingsHolder = transform(userInfoAsJson)
            Pair(userInfoSettingsHolder.mandatory, createResolvers(userInfoSettingsHolder))
        } else {
            if (defaultSettingsHolder != null) {
                Pair(defaultSettingsHolder.mandatory, defaultSettingsHolder.resolvers)
            } else {
                Pair(false, null)
            }
        }
}
