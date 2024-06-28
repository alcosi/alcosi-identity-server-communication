package com.alcosi.identity.dto.domain

import com.alcosi.identity.dto.api.IdentityChangeAccountRq


open class IdentityDomainChangeAccount(
        val fullName: IdentityDomainName? = null,
        val email: String? = null,
        val phone: String? = null,
        val claims: List<IdentityDomainClaim>? = null,
        val photoBase64: String? = null,
    ){
    fun toApi(): IdentityChangeAccountRq=IdentityChangeAccountRq(fullName?.toApi(), email, phone, claims?.map { it.toApi() },photoBase64)
}