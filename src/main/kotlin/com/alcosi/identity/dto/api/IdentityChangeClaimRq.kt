package com.alcosi.identity.dto.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
open class IdentityChangeClaimRq
    @JsonCreator
    constructor(
        @JsonProperty("oldClaim")
        val oldClaim: IdentityServerClaim?,
        @JsonProperty("newClaim")
        val newClaim: IdentityServerClaim?,
    )
