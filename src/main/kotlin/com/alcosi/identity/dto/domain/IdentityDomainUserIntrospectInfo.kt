package com.alcosi.identity.dto.domain

import com.alcosi.identity.dto.api.IdentityUserInfoAccountRs

open class IdentityDomainUserIntrospectInfo(
    val id: String,
    val twoFaEnabled: Boolean,
    val hasTOTP: Boolean,
)
fun IdentityUserInfoAccountRs.toDomain()=IdentityDomainUserIntrospectInfo(id,twoFaEnabled?:false,hasAuthenticator?:false)