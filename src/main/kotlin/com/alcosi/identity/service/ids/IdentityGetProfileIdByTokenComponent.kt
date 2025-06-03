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

package com.alcosi.identity.service.ids

import com.alcosi.identity.config.IdentityServerProperties
import com.alcosi.identity.dto.api.IdentityUserInfoAccountRs
import com.alcosi.identity.dto.domain.IdentityDomainUserIntrospectInfo
import com.alcosi.identity.dto.domain.toDomain
import com.alcosi.identity.exception.IdentityException
import com.alcosi.identity.exception.ids.IdentityExpiredOrInvalidRefreshTokenException
import com.alcosi.identity.exception.ids.IdentityGetAccountIdByTokenException
import com.alcosi.identity.service.error.parseExceptionAndExchange
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.breninsul.logging2.HttpConfigHeaders
import org.springframework.web.client.RestClient
import java.util.logging.Level
import java.util.logging.Logger

/**
 * The `IdentityGetProfileIdByTokenComponent` interface extends the `IdentityProfileIdByTokenProvider`
 * interface and provides a method to retrieve the user ID associated with a token.
 */
interface IdentityGetProfileIdByTokenComponent:IdentityProfileIdByTokenProvider {
    /**
     * Retrieves the user ID associated with the provided token.
     *
     * @param token The authentication token.
     * @return The profile ID with info about two auth associated with the
     *     token.
     * @throws IdentityExpiredOrInvalidRefreshTokenException If the token is expired
     *     or invalid.
     * @throws IdentityGetAccountIdByTokenException If an error occurs while
     *     retrieving the user ID.
     * @throws IdentityException If an unexpected exception occurs while
     *     processing the request.
     */
    fun getProfileInfo(token: String): IdentityDomainUserIntrospectInfo


    /**
     * The `IdentityGetProfileIdByTokenComponent` class is responsible for
     * retrieving the user ID from the Identity server using the provided
     * token.
     *
     * @property mappingHelper The ObjectMapper used for mapping JSON responses
     *     to objects.
     * @property webClient The RestClient used for making HTTP requests.
     * @property properties The IdentityServerProperties.Ids object containing
     *     server configuration properties.
     */
    open class Implementation(
        protected open val mappingHelper: ObjectMapper,
        protected open val properties: IdentityServerProperties,
        protected open val webClient: RestClient,
    ) : IdentityGetProfileIdByTokenComponent {

        /**
         * The `getUserInfoUri` property represents the URI used to retrieve user
         * information using a token.
         *
         * @property getUserInfoUri The URI for retrieving user information.
         */
        protected open val getUserInfoUri = "${properties.ids.uri}/connect/userinfo"

        /**
         * This property represents a logger instance for logging messages. It is
         * used for logging messages related to the current class.
         */
        protected open val logger: Logger = Logger.getLogger(this.javaClass.name)

        /**
         * Retrieves the user ID associated with the provided token.
         *
         * @param token The authentication token.
         * @return The profile ID with info about two auth associated with the
         *     token.
         * @throws IdentityExpiredOrInvalidRefreshTokenException If the token is expired
         *     or invalid.
         * @throws IdentityGetAccountIdByTokenException If an error occurs while
         *     retrieving the user ID.
         * @throws IdentityException If an unexpected exception occurs while
         *     processing the request.
         */
        override fun getProfileInfo(token: String): IdentityDomainUserIntrospectInfo {
            try {
                return webClient
                    .get()
                    .uri(getUserInfoUri)
                    .header("Authorization", "Bearer $token")
                    .headers { if (properties.disableBodyLoggingWithToken) it.set(HttpConfigHeaders.LOG_REQUEST_HEADERS,"false") }
                    .parseExceptionAndExchange { _, clientResponse ->
                        val body = clientResponse.bodyTo(String::class.java)
                        if (clientResponse.statusCode.is2xxSuccessful) {
                            val account = mappingHelper.readValue(body, IdentityUserInfoAccountRs::class.java)
                            return@parseExceptionAndExchange account.toDomain()
                        } else if (clientResponse.statusCode.value() == 401) {
                            throw IdentityExpiredOrInvalidRefreshTokenException(clientResponse.statusCode.value(), body)
                        } else {
                            throw IdentityGetAccountIdByTokenException(clientResponse.statusCode.value(), body)
                        }
                    }
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw if (t is IdentityException) t else IdentityGetAccountIdByTokenException(exception = t)
            }
        }

        /**
         * Retrieves the identity profile ID associated with the provided token.
         *
         * @param token The authentication token.
         * @return The identity profile ID.
         * @throws IdentityExpiredOrInvalidRefreshTokenException If the token is expired or invalid.
         * @throws IdentityGetAccountIdByTokenException If an error occurs while retrieving the profile ID.
         * @throws IdentityException If an unexpected exception occurs while processing the request.
         */
        override fun getIdentityProfileIdByToken(token: String): String {
            return getProfileInfo(token).id
        }
    }
}
