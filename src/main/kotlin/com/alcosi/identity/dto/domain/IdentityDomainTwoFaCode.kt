package com.alcosi.identity.dto.domain

import com.alcosi.identity.dto.api.Identity2FaCodeRs

/**
 * IdentityDomainTwoFaCode represents a two-factor authentication code used in the identity domain.
 *
 * @property code The code associated with the two-factor authentication.
 * @property token The token used for authorization.
 *
 * @constructor Creates an instance of IdentityDomainTwoFaCode.
 *
 * @param code The code associated with the two-factor authentication.
 * @param token The token used for authorization.
 */
open class IdentityDomainTwoFaCode(
    code: String?,
    token: String?,
) : IdentityDomainActivationToken(code, token) {
}
 fun IdentityDomainTwoFaCode?.toApi() = Identity2FaCodeRs(this?.code, this?.token)

fun Identity2FaCodeRs?.toDomain() = IdentityDomainTwoFaCode(this?.code, this?.token)
