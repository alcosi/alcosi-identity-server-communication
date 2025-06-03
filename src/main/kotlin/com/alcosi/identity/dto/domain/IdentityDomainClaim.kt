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

package com.alcosi.identity.dto.domain

import com.alcosi.identity.dto.api.IdentityServerClaim

/**
 * Represents a claim associated with an identity domain.
 *
 * @property type The type of the claim.
 * @property value The value of the claim.
 */
open class IdentityDomainClaim(
    val type: String,
    val value: String?,
)
fun IdentityDomainClaim.toApi(): IdentityServerClaim = IdentityServerClaim(this.type , this.value)
fun IdentityServerClaim.toDomain(): IdentityDomainClaim = IdentityDomainClaim(this.type, this.value)
