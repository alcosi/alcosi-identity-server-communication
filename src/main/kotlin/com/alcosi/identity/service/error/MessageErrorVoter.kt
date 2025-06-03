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

import org.springframework.web.client.RestClient

/**
 * Represents a functional interface for voting on whether an error message should be handled.
 *
 * This interface extends the ErrorVoter interface.
 */
fun interface MessageErrorVoter : ErrorVoter {
    override fun vote( response: RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse): Boolean {
        return if (response.statusCode.is2xxSuccessful) {
            false
        } else {
            vote( response.bodyTo(String::class.java), response.statusCode.value())
        }
    }

    fun vote( message: String?, statusCode: Int): Boolean
}