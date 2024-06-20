package com.alcosi.identity.dto.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
open class IdentityUserInfoAccountRs(
    @JsonProperty("sub") val id: String,
    @JsonProperty("2fa_enabled") val twoFaEnabled: Boolean?,
    @JsonProperty("has_authenticator") val hasAuthenticator: Boolean?,
)
