package com.alcosi.identity.dto.domain

import com.alcosi.identity.dto.api.IdentityResetCode

/**
 * Represents an Identity Domain Reset Code used for resetting a profile's password.
 *
 * @property code The code associated with the reset code.
 * @property token The token used for authorization.
 * @property password The user's new password.
 * @constructor Creates an instance of IdentityDomainResetCode.
 */
open class IdentityDomainResetCode(
    code: String?,
    token: String?,
    val password: String?,
) : IdentityDomainActivationToken(code, token)
fun IdentityDomainResetCode?.toApi() = IdentityResetCode(this?.code, this?.token, this?.password)

fun IdentityResetCode?.toDomain() = IdentityDomainResetCode(this?.code, this?.token, this?.password)
