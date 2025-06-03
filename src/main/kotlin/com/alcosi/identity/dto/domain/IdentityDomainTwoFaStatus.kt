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

import com.alcosi.identity.dto.api.Identity2FaRs

/**
 * Represents the two-factor authentication status for an identity domain.
 *
 * @param totp Specifies whether Time-based One-Time Password (TOTP) is enabled for the domain.
 * @param twoFa Specifies whether two-factor authentication is enabled for the domain.
 * @param recoveryCodesLeft The number of recovery codes left for the domain. Can be null.
 * @param accountConfirmed Specifies whether the domain's account has been confirmed. Can be null.
 */
open class IdentityDomainTwoFaStatus(
    val totp: Boolean,
    val twoFa: Boolean,
    val recoveryCodesLeft: Int?,
    val accountConfirmed: Boolean?,
)

fun IdentityDomainTwoFaStatus?.toApi() = this?.let { acc -> Identity2FaRs(acc.totp, acc.twoFa, acc.recoveryCodesLeft, acc.accountConfirmed) }

fun Identity2FaRs.toDomain() = this.let { acc -> IdentityDomainTwoFaStatus(acc.hasAuthenticator ?: false, acc.is2faEnabled ?: false, acc.recoveryCodesLeft, acc.accountConfirmed) }
