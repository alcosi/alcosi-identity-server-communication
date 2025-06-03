/*
 *
 *  * Copyright (c) 2025 Alcosi Group Ltd. and affiliates.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 */

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
