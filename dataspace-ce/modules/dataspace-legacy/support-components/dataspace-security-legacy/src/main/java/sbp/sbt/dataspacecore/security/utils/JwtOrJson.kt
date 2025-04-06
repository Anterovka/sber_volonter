package sbp.sbt.dataspacecore.security.utils

enum class JwtOrJson {
    JWT, JSON;

    companion object {
        fun byString(value: String): JwtOrJson {
            if ("JWT".equals(value, ignoreCase = true)) {
                return JWT
            }
            if ("JSON".equals(value, ignoreCase = true)) {
                return JSON
            }
            throw IllegalArgumentException("Unknown DataType value: $value")
        }
    }
}
