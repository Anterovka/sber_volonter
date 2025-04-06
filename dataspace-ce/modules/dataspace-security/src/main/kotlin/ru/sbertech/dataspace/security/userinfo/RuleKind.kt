package ru.sbertech.dataspace.security.userinfo

enum class RuleKind(
    val value: String,
) {
    RAW_HEADER("raw-header"),
    JWT("jwt"),
    JSON("json"),
    BASE_64_ENCODED_RAW_HEADER("base64-encoded-raw-header"),
    BASE_64_ENCODED_JSON("base64-encoded-json"),
    ;

    companion object {
        fun findByValue(value: String): RuleKind? = values().find { enumElem -> enumElem.value == value }
    }
}
