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

import com.alcosi.identity.dto.api.IdentityServerFullName

/**
 * Represents a person's identity domain name.
 *
 * @property firstName The first name of the person.
 * @property lastName The last name of the person.
 * @property middleName The middle name of the person.
 */
open class IdentityDomainName(
    val firstName: String?,
    val lastName: String?,
    val middleName: String?,
)

fun IdentityDomainName?.toApi() = this?.let { acc->IdentityServerFullName(acc.firstName, acc.lastName, acc.middleName)}

fun IdentityServerFullName?.toDomain() = IdentityDomainName(this?.firstName, this?.lastName, this?.middleName)
