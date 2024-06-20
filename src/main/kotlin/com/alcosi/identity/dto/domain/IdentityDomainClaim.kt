package com.alcosi.identity.dto.domain

import com.alcosi.identity.dto.api.IdentityServerClaim

/**
 * Represents a claim associated with an identity domain.
 *
 * @property type The type of the claim.
 * @property value The value of the claim.
 */
open class IdentityDomainClaim(
    val type: String,
    val value: String?,
)
fun IdentityDomainClaim.toApi(): IdentityServerClaim = IdentityServerClaim(this.type , this.value)
fun IdentityServerClaim.toDomain(): IdentityDomainClaim = IdentityDomainClaim(this.type, this.value)
