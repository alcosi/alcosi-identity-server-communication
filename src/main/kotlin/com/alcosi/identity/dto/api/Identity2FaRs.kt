package com.alcosi.identity.dto.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
open class Identity2FaRs(
    val hasAuthenticator: Boolean?,
    val is2faEnabled: Boolean?,
    val recoveryCodesLeft: Int?,
    val accountConfirmed: Boolean?,
)
