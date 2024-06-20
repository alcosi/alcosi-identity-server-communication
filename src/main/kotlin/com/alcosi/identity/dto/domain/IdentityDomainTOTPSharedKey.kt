package com.alcosi.identity.dto.domain

import com.alcosi.identity.dto.api.Identity2FaAuthenticatorGenerateCodeRs

/**
 * Represents the TOTP (Time-Based One-Time Password) shared key for two-factor authentication.
 *
 * @param sharedKey The shared key for TOTP.
 * @param authenticatorUri The URI of the authenticator.
 * @param qrCode The QR code for the authenticator.
 */
open class IdentityDomainTOTPSharedKey(
    val sharedKey: String?,
    val authenticatorUri: String?,
    val qrCode: String?,
)

fun Identity2FaAuthenticatorGenerateCodeRs.toDomain() = IdentityDomainTOTPSharedKey(sharedKey, authenticatorUri, qrCode)

fun IdentityDomainTOTPSharedKey.toApi() = IdentityDomainTOTPSharedKey(sharedKey, authenticatorUri, qrCode)
