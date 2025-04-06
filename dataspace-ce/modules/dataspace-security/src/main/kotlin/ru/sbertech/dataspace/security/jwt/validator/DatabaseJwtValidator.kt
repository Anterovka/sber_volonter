package ru.sbertech.dataspace.security.jwt.validator

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.util.StringUtils
import ru.sbertech.dataspace.security.jwt.validator.JwtValidator.TokenValidatorSettings
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson
import sbp.sbt.dataspacecore.security.utils.SecurityUtils
import java.lang.Boolean

class DatabaseJwtValidator(
    private val entitiesReadAccessJson: EntitiesReadAccessJson,
) : DynamicJwtValidator() {
    private val searchNode: JsonNode

    init {
        val objectMapper = ObjectMapper()
        val props = objectMapper.createArrayNode()
        props.add("key")
        props.add("value")
        searchNode =
            objectMapper
                .createObjectNode()
                .put("type", "SysAdminSettings")
                .put("cond", "it.key \$in ['jwks', 'iss', 'aud', 'expDelta', 'nbfDelta']")
                .set("props", props)
    }

    // PostConstruct не отрабатывает из-за того, что при автосоздании в контексте нет токена и падает SecurityDriver
    //    @PostConstruct
    public override fun recreate() {
        val res: JsonNode
        val dsToken = SecurityUtils.getCurrentToken()
        try {
            dsToken.systemRead = Boolean.TRUE
            res = entitiesReadAccessJson.searchEntities(searchNode)
        } finally {
            dsToken.systemRead = Boolean.FALSE
        }
        val elems = res["elems"]
        val newSettings = TokenValidatorSettings()
        if (!elems.isEmpty) {
            for (elem in elems) {
                val key = elem["props"]["key"].asText()
                val value = elem["props"]["value"].asText()
                // Пропускаем пустые значения свойств
                if (StringUtils.isEmpty(value)) {
                    continue
                }
                when (key) {
                    JwtValidator.ISS -> newSettings.iss = value
                    JwtValidator.AUD -> newSettings.aud = value
                    JwtValidator.JWKS -> newSettings.jwks = value
                    JwtValidator.EXP_DELTA -> newSettings.expDelta = value.toLong()
                    JwtValidator.NBF_DELTA -> newSettings.nbfDelta = value.toLong()
                }
            }
        }
        // Даже если настройки полностью пустые все равно сетим, т.к. мы не знаем, может так пользователь задумал.
        tokenValidator = JwtValidator(newSettings)
    }
}
