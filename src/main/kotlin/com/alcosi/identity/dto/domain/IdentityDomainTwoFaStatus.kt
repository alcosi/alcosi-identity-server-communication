package com.alcosi.identity.dto.domain

import com.alcosi.identity.dto.api.Identity2FaRs

/**
 * Represents the two-factor authentication status for an identity domain.
 *
 * @param totp Specifies whether Time-based One-Time Password (TOTP) is enabled for the domain.
 * @param twoFa Specifies whether two-factor authentication is enabled for the domain.
 * @param recoveryCodesLeft The number of recovery codes left for the domain. Can be null.
 * @param accountConfirmed Specifies whether the domain's account has been confirmed. Can be null.
 */
open class IdentityDomainTwoFaStatus(
    val totp: Boolean,
    val twoFa: Boolean,
    val recoveryCodesLeft: Int?,
    val accountConfirmed: Boolean?,
)

fun IdentityDomainTwoFaStatus?.toApi() = this?.let { acc -> Identity2FaRs(acc.totp, acc.twoFa, acc.recoveryCodesLeft, acc.accountConfirmed) }

fun Identity2FaRs.toDomain() = this.let { acc -> IdentityDomainTwoFaStatus(acc.hasAuthenticator ?: false, acc.is2faEnabled ?: false, acc.recoveryCodesLeft, acc.accountConfirmed) }
