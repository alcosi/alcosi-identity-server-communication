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

package com.alcosi.identity.service.error

/**
 * An enumeration representing the possible error types in the Identity Parser IDS.
 * Each error type corresponds to a specific exception in the Identity Parser IDS system.
 * The error types include:
 * - NO_PASSWORD: Indicates that the profile password is not set.
 * - LOCKED: Indicates that the account is locked.
 * - NOT_ACTIVATED: Indicates that the account is not activated.
 * - INVALID_CREDENTIALS: Indicates that the account credentials are not valid.
 * - TWO_FA: Indicates that 2FA (Two-Factor Authentication) is required.
 * - AUTHENTICATOR: Indicates an error related to the authenticator.
 * - UNKNOWN: Indicates an unknown error.
 */
enum class IdentityParserIdsErrorType {
    NO_PASSWORD,
    LOCKED,
    NOT_ACTIVATED,
    INVALID_CREDENTIALS,
    TWO_FA,
    AUTHENTICATOR,
    UNKNOWN,
}