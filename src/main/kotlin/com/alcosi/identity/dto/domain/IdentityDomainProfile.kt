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

import com.alcosi.identity.dto.api.IdentityAccount

/**
 * This class represents an identity domain account.
 *
 * @property id The ID of the account.
 * @property name The name of the account.
 * @property email The email of the account.
 * @property phone The phone number of the account.
 * @property claims The list of claims associated with the account.
 * @property photoBase64 The base64 encoded photo of the account.
 */
open class IdentityDomainAccount(
    val id: String?,
    val name: IdentityDomainName?,
    val email: String?,
    val phone: String?,
    val claims: List<IdentityDomainClaim>,
    val photoBase64: String?,
)

fun IdentityDomainAccount?.toApi() = IdentityAccount(this?.id, this?.name.toApi(), this?.email, this?.phone, this?.claims?.map { it.toApi() } ?: listOf(), this?.photoBase64)

fun IdentityAccount?.toDomain() = IdentityDomainAccount(this?.id, this?.fullName.toDomain(), this?.email, this?.phoneNumber, this?.claims?.map { it.toDomain() } ?: listOf(), this?.photo)
