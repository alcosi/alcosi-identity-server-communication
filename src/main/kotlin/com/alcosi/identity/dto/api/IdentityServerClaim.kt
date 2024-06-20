package com.alcosi.identity.dto.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
open class IdentityServerClaim
@JsonCreator
constructor(
    @JsonProperty("type") val type: String,
    @JsonProperty("value") val value: String?,
)
