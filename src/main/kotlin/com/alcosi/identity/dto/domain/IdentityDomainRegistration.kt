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

import com.alcosi.identity.dto.api.IdentityRegistrationRq

/**
 * Represents a domain registration entity.
 *
 * @property name The name of the identity domain.
 * @property contact Phone or Email The contact information for the domain registration.
 * @property password The password for the domain registration.
 * @property claims The list of claims associated with the domain registration.
 */
open class IdentityDomainRegistration(
    val name: IdentityDomainName?,
    val contact: String?,
    val password: String?,
    val claims: List<IdentityDomainClaim>,
)

/**
 * Converts an instance of [IdentityDomainRegistration] to an instance of [IdentityRegistrationRq].
 *
 * @param name The name of the identity domain.
 * @param contact The contact information for the domain registration.
 * @param password The password for the domain registration.
 * @param claims The list of claims associated with the domain registration.
 * @return An instance of [IdentityRegistrationRq] representing the converted object.
 */
fun IdentityDomainRegistration.toApi() = IdentityRegistrationRq(name.toApi(),contact,password,claims.map { it.toApi() })
