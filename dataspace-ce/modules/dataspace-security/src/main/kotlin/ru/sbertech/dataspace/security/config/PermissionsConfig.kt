package ru.sbertech.dataspace.security.config

class PermissionsConfig {
    enum class SourceType {
        DB,
        FILE,
    }

    var source: SourceType? = null
    var path: String? = null
}
