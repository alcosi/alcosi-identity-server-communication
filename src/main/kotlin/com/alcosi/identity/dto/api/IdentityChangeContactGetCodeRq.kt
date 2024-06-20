package com.alcosi.identity.dto.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
open class IdentityChangeContactGetCodeRq(
    @JsonProperty("op")
    val operation: String,
    @JsonProperty("value")
    val value: String,
    @JsonProperty("path")
    val path: String,
) {
    constructor(operation: Operation, value: String, type: ContactType) : this(operation.identityName, value, type.objectPath)

    enum class Operation(val identityName: String) {
        REPLACE("replace"),
        ADD("add"),
        REMOVE("remove"),
    }

    enum class ContactType(val objectPath: String, val uriPath: String) {
        EMAIL("/email", "email"),
        PHONE("/phoneNumber", "phone"),
    }
}
