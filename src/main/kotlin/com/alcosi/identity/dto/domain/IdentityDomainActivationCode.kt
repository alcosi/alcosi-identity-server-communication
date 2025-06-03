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

import com.alcosi.identity.dto.api.IdentityActivationCode

/**
 * IdentityDomainActivationToken represents an activation token used in identity domain.
 *
 * @property code The code associated with the activation token.
 * @property token The token used for authorization.
 */
open class IdentityDomainActivationToken(
    val code: String?,
    val token: String?,
)

fun IdentityDomainActivationToken?.toApi(): IdentityActivationCode = IdentityActivationCode(this?.code, this?.token)

fun IdentityActivationCode?.toDomain() = IdentityDomainActivationToken(this?.code, this?.token)
