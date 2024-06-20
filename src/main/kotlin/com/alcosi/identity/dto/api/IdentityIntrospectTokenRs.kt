package com.alcosi.identity.dto.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
open class IdentityIntrospectTokenRs(
    val active: Boolean = false,
    val sub: String?,
    val iss: String?,
    val scope: String?,
    val idp: String?,
    val amr: String?,
    val jti: String?,
    val sid: String?,
    @JsonProperty("2fa_enabled") val twoFaEnabled: Boolean?,
    @JsonProperty("has_authenticator") val hasAuthenticator: Boolean?,
    val nbf: Long?,
    val exp: Long?,
    val iat: Long?,
    @JsonProperty("auth_time") val authTime: Long?,
    val aud: List<String>?,
    val clientId: String?,
)
