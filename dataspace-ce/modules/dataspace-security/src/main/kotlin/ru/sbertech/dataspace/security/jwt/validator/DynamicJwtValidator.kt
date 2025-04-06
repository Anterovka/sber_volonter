package ru.sbertech.dataspace.security.jwt.validator

/** Токен валидатор, настройки которого могут меняться с течением времени  */
abstract class DynamicJwtValidator : TokenValidator {
    @JvmField
    protected var tokenValidator: JwtValidator? = null

    protected abstract fun recreate()

    override fun validate(token: String) {
        if (tokenValidator == null) {
            recreate()
            if (tokenValidator == null) {
                throw SecurityException("Couldn't create first DynamicTokenValidator, check logs")
            }
        }
        tokenValidator!!.validate(token)
    }
}
