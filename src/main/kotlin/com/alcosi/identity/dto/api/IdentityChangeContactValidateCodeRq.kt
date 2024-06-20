package com.alcosi.identity.dto.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
open class IdentityChangeContactValidateCodeRq(
    @JsonProperty("emailOrPhone")
    val id: String,
    @JsonProperty("code")
    val code: String,
)
