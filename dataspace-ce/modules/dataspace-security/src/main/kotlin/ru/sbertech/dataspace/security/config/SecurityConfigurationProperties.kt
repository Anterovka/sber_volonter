package ru.sbertech.dataspace.security.config

class SecurityConfigurationProperties {
    var jwt = JwtConfig()
    var jwks = JwksConfig()
    var graphql = GraphQLConfig()
}
