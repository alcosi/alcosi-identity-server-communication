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

import java.time.LocalDateTime

/**
 * Represents the Auth token class.
 *
 * @property accessToken the access token.
 * @property refreshToken the refresh token.
 * @property creationTime the creation time of the token.
 * @property expiresIn the expiration time of the token.
 * @property scopes the scopes of the token.
 * @property validTill the valid till time of the token.
 * @property success indicates whether the token retrieval was successful.
 * @property noSuccessReason the reason for the token retrieval failure.
 */
open class IdentityDomainToken(
    val accessToken: String,
    val refreshToken: String?,
    val creationTime: LocalDateTime,
    val expiresIn: Int,
    val scopes: List<String>,
    val validTill: LocalDateTime = creationTime.plusSeconds(expiresIn.toLong()),
    val success: Boolean = true,
    val noSuccessReason: NoSuccessReason? = null,
){
    enum class NoSuccessReason {
        USE_2FA,
        USE_AUTHENTICATOR,
        ADD_AUTHENTICATOR,
        NO_PASSWORD_IN_REQUEST,
        UNKNOWN,
    }

}
