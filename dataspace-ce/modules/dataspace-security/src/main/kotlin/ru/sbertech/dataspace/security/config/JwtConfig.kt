package ru.sbertech.dataspace.security.config

class JwtConfig {
    inner class ValidationConfig {
        var isDisable: Boolean? = null
            get() = java.lang.Boolean.TRUE == field
    }

    var validation = ValidationConfig()
    var iss: String? = null
    var aud: String? = null
    var expDelta: Long? = null
    var nbfDelta: Long? = null
    var required = false
}
