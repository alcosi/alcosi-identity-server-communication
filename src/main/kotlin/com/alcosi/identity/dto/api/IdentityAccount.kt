package com.alcosi.identity.dto.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
open class IdentityAccount
    @JsonCreator
    constructor(
        @JsonProperty("id")
        val id: String?,
        @JsonProperty("fullName")
        val fullName: IdentityServerFullName?,
        @JsonProperty("email")
        val email: String?,
        @JsonProperty("phoneNumber")
        val phoneNumber: String?,
        val claims: List<IdentityServerClaim>,
        val photo: String?,
    )
