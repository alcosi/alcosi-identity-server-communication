package com.alcosi.identity.dto.domain

import com.alcosi.identity.dto.api.IdentityIntrospectTokenRs

open class IdentityDomainIntrospectedToken(
    val active: Boolean = false,
    val sub: String?,
    val iss: String?,
    val scope: String?,
    val idp: String?,
    val amr: String?,
    val jti: String?,
    val sid: String?,
    val twoFaEnabled: Boolean?,
    val hasTOTP: Boolean?,
    val nbf: Long?,
    val exp: Long?,
    val iat: Long?,
    val authTime: Long?,
    val aud: List<String>?,
    val clientId: String?,
)
fun IdentityIntrospectTokenRs.toDomain()=IdentityDomainIntrospectedToken(active,sub,iss,sub,idp,amr,jti,sid,twoFaEnabled,hasAuthenticator,nbf, exp, iat, authTime, aud, clientId)