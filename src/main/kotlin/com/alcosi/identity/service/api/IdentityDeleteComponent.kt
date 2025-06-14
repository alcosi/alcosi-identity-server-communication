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
import com.alcosi.identity.exception.api.IdentityDeleteException
import com.alcosi.identity.service.error.parseExceptionAndExchange
import com.alcosi.identity.service.token.IdentityClientTokenHolder
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.client.RestClient
import com.alcosi.identity.config.URLPreparation
import java.nio.charset.Charset
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

/**
 * The IdentityDeleteComponent interface represents a component used to delete entities from the Identity Server.
 */
interface IdentityDeleteComponent {
    /**
     * Deletes the entity with the specified ID.
     *
     * @param id The ID of the profile (phone number, email, or ID)
     * @param isPermanent Specifies whether the deletion should be permanent. Default value is true.
     * @throws IdentityDeleteException if an error occurs during the deletion process.
     */
    fun delete(id: UUID, isPermanent: Boolean = true)


    /**
     * This class represents a component used to delete entities from the Identity Server.
     *
     * @property tokenHolder The client token holder for retrieving access tokens.
     * @property properties The properties for the Identity Server API.
     * @property mappingHelper The object mapper for mapping JSON responses.
     * @property restClient The REST client for making API requests.
     */
    open class Implementation(
        protected open val tokenHolder: IdentityClientTokenHolder,
        protected open val properties: IdentityServerProperties,
        protected open val mappingHelper: ObjectMapper,
        protected open val restClient: RestClient,
    ) : IdentityDeleteComponent {
        /**
         * This variable represents a URI template for the user deletion endpoint.
         * It is composed of the base URI from the IdentityServerProperties class
         * and additional path segments and query parameters.
         *
         * The base URI is retrieved from the properties object.
         * The {id} path segment is replaced with the provided user ID.
         * The {isPermanent} query parameter is replaced with the provided boolean value.
         */
        protected open val uri = "${properties.api.uri}/user/{id}?isPermanent={isPermanent}"

        /**
         * This property represents a logger instance for logging messages. It is
         * used for logging messages related to the current class.
         */
        protected open val logger: Logger = Logger.getLogger(this.javaClass.name)

        /**
         * Deletes the entity with the specified ID.
         *
         * @param id The ID of the profile (phone number, email, or ID)
         * @param isPermanent Specifies whether the deletion should be permanent. Default value is true.
         * @throws IdentityDeleteException if an error occurs during the deletion process.
         */
        override fun delete(id: UUID, isPermanent: Boolean) {
            try {
                restClient
                    .delete()
                    .uri(uri,mapOf<String,String>("id" to id.toString(),"isPermanent" to isPermanent.toString()))
                    .header("Authorization", "Bearer ${tokenHolder.getAccessToken()}")
                    .header("x-api-version", properties.api.apiVersion)
                    .parseExceptionAndExchange { _, clientResponse ->
                        if (clientResponse.statusCode.is2xxSuccessful) {
                            return@parseExceptionAndExchange
                        } else {
                            val body = clientResponse.bodyTo(String::class.java)
                            throw IdentityDeleteException(clientResponse.statusCode.value(), body)
                        }
                    }
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw if (t is IdentityException) t else IdentityDeleteException(exception = t)
            }
        }
    }
}