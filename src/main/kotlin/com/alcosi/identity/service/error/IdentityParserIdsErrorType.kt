package com.alcosi.identity.service.error

/**
 * An enumeration representing the possible error types in the Identity Parser IDS.
 * Each error type corresponds to a specific exception in the Identity Parser IDS system.
 * The error types include:
 * - NO_PASSWORD: Indicates that the profile password is not set.
 * - LOCKED: Indicates that the account is locked.
 * - NOT_ACTIVATED: Indicates that the account is not activated.
 * - INVALID_CREDENTIALS: Indicates that the account credentials are not valid.
 * - TWO_FA: Indicates that 2FA (Two-Factor Authentication) is required.
 * - AUTHENTICATOR: Indicates an error related to the authenticator.
 * - UNKNOWN: Indicates an unknown error.
 */
enum class IdentityParserIdsErrorType {
    NO_PASSWORD,
    LOCKED,
    NOT_ACTIVATED,
    INVALID_CREDENTIALS,
    TWO_FA,
    AUTHENTICATOR,
    UNKNOWN,
}