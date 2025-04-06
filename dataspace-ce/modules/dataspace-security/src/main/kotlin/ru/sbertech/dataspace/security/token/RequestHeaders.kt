package ru.sbertech.dataspace.security.token

enum class RequestHeaders(
    val headerName: String,
) {
    IV_USER("iv-user"),
    IV_GROUPS("iv-groups"),
    OTT_TOKEN("ott-token"),
    OTT_HASH("ott-hash"),
    JWT("Authorization"),
}
