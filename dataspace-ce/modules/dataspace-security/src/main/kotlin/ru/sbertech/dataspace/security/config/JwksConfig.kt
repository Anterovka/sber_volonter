package ru.sbertech.dataspace.security.config

class JwksConfig {
    enum class JwksSource {
        PROPERTY,
        FILE,
        DB,
        KEYCLOAK,
        IAM,
        URL,
    }

    var source: JwksSource? = null
    var value: String? = null
}
