package ru.sbertech.dataspace.security.jwt.validator

interface TokenValidator {
    fun validate(token: String)
}
