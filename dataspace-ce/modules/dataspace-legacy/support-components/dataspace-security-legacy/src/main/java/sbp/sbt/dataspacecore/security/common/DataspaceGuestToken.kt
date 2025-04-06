package sbp.sbt.dataspacecore.security.common

class DataspaceGuestToken: DataspaceAuthenticationToken {
    private val serialVersionUID = 1L

    constructor() : super() {
        isAuthenticated = false
    }
}