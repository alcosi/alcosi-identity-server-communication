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
 * Enum class representing different types of API errors that can occur in the IdentityParserApi.
 *
 * The possible error types are:
 *
 * 1. PROFILE_IS_ALREADY_ACTIVATED - Indicates that the profile is already activated.
 *
 * 2. PROFILE_IS_ALREADY_REGISTERED - Indicates that the profile is already registered.
 *
 * 3. PASSWORD_IS_NOT_STRONG_ENOUGH - Indicates that the password is not strong enough.
 *
 * 4. PROFILE_NOT_EXIST - Indicates that the profile does not exist.
 *
 * 5. WRONG_ACTIVATION_CODE - Indicates that the activation code is incorrect.
 *
 * 6. AUTHENTIFICATOR_CODE_IS_INVALID - Indicates that the authenticator code is invalid.
 *
 * 7. UNKNOWN - Indicates an unknown error type.
 */
enum class IdentityParserApiErrorType {
    PROFILE_IS_ALREADY_ACTIVATED,
    PROFILE_IS_ALREADY_REGISTERED,
    PASSWORD_IS_NOT_STRONG_ENOUGH,
    PROFILE_NOT_EXIST,
    INVALID_CODE,
    AUTHENTIFICATOR_CODE_IS_INVALID,
    UNKNOWN,
}