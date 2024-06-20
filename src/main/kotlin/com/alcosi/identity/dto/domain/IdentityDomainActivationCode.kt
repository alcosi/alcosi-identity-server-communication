package com.alcosi.identity.dto.domain

import com.alcosi.identity.dto.api.IdentityActivationCode

/**
 * IdentityDomainActivationToken represents an activation token used in identity domain.
 *
 * @property code The code associated with the activation token.
 * @property token The token used for authorization.
 */
open class IdentityDomainActivationToken(
    val code: String?,
    val token: String?,
)

fun IdentityDomainActivationToken?.toApi(): IdentityActivationCode = IdentityActivationCode(this?.code, this?.token)

fun IdentityActivationCode?.toDomain() = IdentityDomainActivationToken(this?.code, this?.token)
