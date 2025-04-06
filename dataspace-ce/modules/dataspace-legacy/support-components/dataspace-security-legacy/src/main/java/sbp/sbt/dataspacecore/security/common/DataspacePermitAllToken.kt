package sbp.sbt.dataspacecore.security.common

class DataspacePermitAllToken: DataspaceAuthenticationToken() {
    init {
        suppressTenantCheck = true
        suppressGqlCheck = true
        supressSecurityDriverCheck = true
    }
}
