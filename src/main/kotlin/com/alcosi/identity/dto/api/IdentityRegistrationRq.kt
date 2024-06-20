package com.alcosi.identity.dto.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
open class IdentityRegistrationRq
    @JsonCreator
    constructor(
        @JsonProperty("fullName")
        val fullName: IdentityServerFullName?,
        @JsonProperty("emailOrPhone")
        val emailOrPhone: String?,
        @JsonProperty("password")
        val password: String?,
        val claims: List<IdentityServerClaim>,
    )
