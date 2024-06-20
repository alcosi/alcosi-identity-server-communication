package com.alcosi.identity.service.error

/**
 * Enum class representing different types of API errors that can occur in the IdentityParserApi.
 *
 * The possible error types are:
 *
 * 1. PROFILE_IS_ALREADY_ACTIVATED - Indicates that the profile is already activated.
 *
 * 2. PROFILE_IS_ALREADY_REGISTERED - Indicates that the profile is already registered.
 *
 * 3. PASSWORD_IS_NOT_STRONG_ENOUGH - Indicates that the password is not strong enough.
 *
 * 4. PROFILE_NOT_EXIST - Indicates that the profile does not exist.
 *
 * 5. WRONG_ACTIVATION_CODE - Indicates that the activation code is incorrect.
 *
 * 6. AUTHENTIFICATOR_CODE_IS_INVALID - Indicates that the authenticator code is invalid.
 *
 * 7. UNKNOWN - Indicates an unknown error type.
 */
enum class IdentityParserApiErrorType {
    PROFILE_IS_ALREADY_ACTIVATED,
    PROFILE_IS_ALREADY_REGISTERED,
    PASSWORD_IS_NOT_STRONG_ENOUGH,
    PROFILE_NOT_EXIST,
    WRONG_ACTIVATION_CODE,
    AUTHENTIFICATOR_CODE_IS_INVALID,
    UNKNOWN,
}