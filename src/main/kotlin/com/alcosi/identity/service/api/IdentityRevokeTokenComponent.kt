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

package com.alcosi.identity.service.api

import com.alcosi.identity.config.IdentityServerProperties
import com.alcosi.identity.exception.IdentityException
import com.alcosi.identity.exception.api.IdentityRevokeException
import com.alcosi.identity.service.error.parseExceptionAndExchange
import com.alcosi.identity.service.token.IdentityClientTokenHolder
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.client.RestClient
import java.util.logging.Level
import java.util.logging.Logger

/**
 * The IdentityRevokeTokenComponent interface provides a mechanism to revoke tokens from the identity server.
 */
interface IdentityRevokeTokenComponent {
    /**
     * Revokes the specified tokens from the identity server.
     *
     * @param ids A list of profile IDs (id,email or phone) to be revoked.
     * @param ip The IP address of the requester.
     * @param userAgent The User-Agent header to be included in the request. It can be null or blank.
     * @throws IdentityRevokeException if an error occurs during the revoke process.
     */
    fun revoke(
        ids: List<String>,
        ip: String,
        userAgent: String?,
    )

    open class Implementation(
        protected open val tokenHolder: IdentityClientTokenHolder,
        protected open val properties: IdentityServerProperties,
        protected open val mappingHelper: ObjectMapper,
        protected open val restClient: RestClient,
    ) : IdentityRevokeTokenComponent {
        /** The URI endpoint for revoking access on the identity server. */
        protected open val revokeUri = "${properties.api.uri}/user/revokeaccess"

        /**
         * This property represents a logger instance for logging messages. It is
         * used for logging messages related to the current class.
         */
        protected open val logger: Logger = Logger.getLogger(this.javaClass.name)

        /**
         * Revokes the specified tokens from the identity server.
         *
         * @param ids A list of profile IDs (id,email or phone) to be revoked.
         * @param ip The IP address of the requester.
         * @param userAgent The User-Agent header to be included in the request. It can be null or blank.
         * @throws IdentityRevokeException if an error occurs during the revoke process.
         */
        override fun revoke(
            ids: List<String>,
            ip: String,
            userAgent: String?,
        ) {
            try {
                return restClient
                    .delete()
                    .uri(revokeUri)
                    .setHeaders(ip, userAgent)
                    .parseExceptionAndExchange { _, clientResponse ->
                        if (clientResponse.statusCode.is2xxSuccessful) {
                            return@parseExceptionAndExchange
                        } else {
                            val body = clientResponse.bodyTo(String::class.java)
                            throw IdentityRevokeException(clientResponse.statusCode.value(), body)
                        }
                    }
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw if (t is IdentityException) t else IdentityRevokeException(exception = t)
            }
        }

        /**
         * Sets the headers for the RestClient RequestHeadersSpec.
         *
         * @param ip The IP address of the requester.
         * @param userAgent The User-Agent header to be included in the request. It can be null or blank.
         * @return The modified RestClient RequestHeadersSpec.
         */
        protected open fun RestClient.RequestHeadersSpec<*>.setHeaders(
            ip: String,
            userAgent: String?,
        ): RestClient.RequestHeadersSpec<*> {
            val ipSpec =
                this
                    .header(properties.ids.ipHeader, ip)
                    .header("Authorization", "Bearer ${tokenHolder.getAccessToken()}")
                    .header("x-api-version", properties.api.apiVersion)
            return if (userAgent.isNullOrBlank()) {
                ipSpec
            } else {
                ipSpec
                    .header(properties.ids.userAgentHeader, userAgent)
            }
        }
    }
}
