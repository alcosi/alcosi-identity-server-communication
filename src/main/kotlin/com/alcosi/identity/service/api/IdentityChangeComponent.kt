package com.alcosi.identity.service.api

import com.alcosi.identity.config.IdentityServerProperties
import com.alcosi.identity.config.URLPreparation
import com.alcosi.identity.dto.domain.IdentityDomainChangeAccount
import com.alcosi.identity.exception.api.IdentityChangeAccountException
import com.alcosi.identity.exception.IdentityException
import com.alcosi.identity.service.error.parseExceptionAndExchange
import com.fasterxml.jackson.databind.ObjectMapper
import com.alcosi.identity.service.token.IdentityClientTokenHolder
import org.springframework.web.client.RestClient

import java.nio.charset.Charset
import java.util.logging.Level
import java.util.logging.Logger

/**
 * The `IdentityChangeComponent` interface defines the methods for changing the identity information of a user.
 */
interface IdentityChangeComponent {
    /**
     * Changes the photo of a user identified by their ID.
     *
     * @param id The ID of the user.
     * @param image The new photo of the user. Can be null.
     * @return True if the photo was successfully changed, false otherwise.
     */
    fun changePhoto(
        id: String,
        image: String?,
    ): Boolean

    /**
     * Changes the account details of a user identified by their ID.
     *
     * @param id The ID of profile (id , or email, or phone).
     * @param rq The request object containing the updated account details.
     * @return True if the account details were successfully changed, false otherwise.
     * @throws IdentityChangeAccountException if there was an error while changing the account details.
     */
    fun change(
        id: String,
        rq: IdentityDomainChangeAccount,
    ): Boolean


    /**
     * The `IdentityChangeComponent` class is responsible for changing the identity information of a user, such as their photo.
     *
     * @property tokenHolder The client token holder for retrieving access tokens.
     * @property properties The properties of the identity server API.
     * @property mappingHelper The object mapper for mapping JSON data.
     * @property restClient The REST client for making HTTP requests. Default is `RestClient.create()`.
     * @property uri The URI template for the user endpoint.
     * @property logger The logger instance for logging messages related to this class.
     *
     * @constructor Creates an `IdentityChangeComponent` instance.
     * @param tokenHolder The client token holder for retrieving access tokens.
     * @param properties The properties of the identity server API.
     * @param mappingHelper The object mapper for mapping JSON data.
     * @param restClient The REST client for making HTTP requests. Default is `RestClient.create()`.
     *
     * @throws IdentityChangeAccountException If an error occurs during the account/user change.
     */
    open class Implementation(
        protected open val tokenHolder: IdentityClientTokenHolder,
        protected open val properties: IdentityServerProperties,
        protected open val mappingHelper: ObjectMapper,
        protected open val restClient: RestClient,
    ) : IdentityChangeComponent {
        /**
         * This variable represents the URI for the user endpoint.
         * The URI is constructed by concatenating the base URI from the properties object with*/
        protected open val uri = "${properties.api.uri}/user/{id}"

        /**
         * This property represents a logger instance for logging messages.
         * It is used for logging messages related to the current class.
         */
        protected open val logger: Logger = Logger.getLogger(this.javaClass.name)

        /**
         * Changes the photo of a user identified by their ID.
         *
         * @param id The ID of the user.
         * @param image The new photo of the user. Can be null.
         * @return True if the photo was successfully changed, false otherwise.
         */
        override fun changePhoto(
            id: String,
            image: String?,
        ): Boolean {
            return change(id, IdentityDomainChangeAccount(photoBase64 = image))
        }

        /**
         * Changes the account details of a user identified by their ID.
         *
         * @param id The ID of profile (id , or email, or phone).
         * @param rq The request object containing the updated account details.
         * @return True if the account details were successfully changed, false otherwise.
         * @throws IdentityChangeAccountException if there was an error while changing the account details.
         */
        override fun change(
            id: String,
            rq: IdentityDomainChangeAccount,
        ): Boolean {
            try {
                return restClient
                    .put()
                    .uri(uri.replace("{id}", URLPreparation.encode(id, Charset.defaultCharset())))
                    .header("Authorization", "Bearer ${tokenHolder.getAccessToken()}")
                    .header("x-api-version", properties.api.apiVersion)
                    .body(rq.toApi())
                    .parseExceptionAndExchange { _, clientResponse ->
                        if (clientResponse.statusCode.is2xxSuccessful) {
                            return@parseExceptionAndExchange true
                        } else {
                            val body = clientResponse.bodyTo(String::class.java)
                            throw IdentityChangeAccountException(clientResponse.statusCode.value(), body)
                        }
                    }
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw if (t is IdentityException) t else IdentityChangeAccountException(exception = t)
            }
        }
    }
}