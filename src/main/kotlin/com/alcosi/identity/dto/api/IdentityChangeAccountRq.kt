package com.alcosi.identity.dto.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
open class IdentityChangeAccountRq
    @JsonCreator
    constructor(
        @JsonProperty("fullName")
        val fullName: IdentityServerFullName? = null,
        @JsonProperty("email")
        val email: String? = null,
        @JsonProperty("phoneNumber")
        val phoneNumber: String? = null,
        val claims: List<IdentityServerClaim>? = null,
        val photo: String? = null,
    )
