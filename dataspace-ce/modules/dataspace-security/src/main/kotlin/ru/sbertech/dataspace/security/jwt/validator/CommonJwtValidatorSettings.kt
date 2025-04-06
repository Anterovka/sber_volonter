package ru.sbertech.dataspace.security.jwt.validator

import com.fasterxml.jackson.databind.ObjectMapper

open class CommonJwtValidatorSettings {
    @JvmField
    var iss: String? = null

    @JvmField
    var aud: String? = null

    @JvmField
    var expDelta: Long = 0

    @JvmField
    var nbfDelta: Long = 0

    @JvmField
    var objectMapper: ObjectMapper? = null

    constructor() {}
    constructor(settings: CommonJwtValidatorSettings) {
        iss = settings.iss
        aud = settings.aud
        expDelta = settings.expDelta
        nbfDelta = settings.nbfDelta
        objectMapper = settings.objectMapper
    }
}
