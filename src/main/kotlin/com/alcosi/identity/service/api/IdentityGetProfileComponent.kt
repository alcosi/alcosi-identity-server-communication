package com.alcosi.identity.service.api

import com.alcosi.identity.config.IdentityServerProperties
import com.alcosi.identity.config.URLPreparation
import com.alcosi.identity.dto.api.IdentityAccount
import com.alcosi.identity.dto.domain.IdentityDomainAccount
import com.alcosi.identity.dto.domain.toDomain
import com.alcosi.identity.exception.IdentityException
import com.alcosi.identity.exception.api.IdentityGetAccountException
import com.alcosi.identity.service.error.parseExceptionAndExchange
import com.alcosi.identity.service.token.IdentityClientTokenHolder
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.client.RestClient
import java.nio.charset.Charset
import java.util.logging.Level
import java.util.logging.Logger

/**
 * The IdentityGetProfileComponent interface represents a component to retrieve the profile of a user.
 */
interface IdentityGetProfileComponent {
    /**
     * Retrieves the profile of a user identified by the given ID.
     *
     * @param id The ID of the profile (phone number, email, or ID)
     * @return The profile information of the user as an [IdentityDomainAccount] object.
     * @throws IdentityGetAccountException If an error occurs while retrieving the profile.
     */
    fun getProfile(id: String): IdentityDomainAccount

    open class Implementation(
        protected open val holder: IdentityClientTokenHolder,
        protected open val properties: IdentityServerProperties,
        protected open val mappingHelper: ObjectMapper,
        protected open val webClient: RestClient,
    ) : IdentityGetProfileComponent {
        protected open val getUserInfoUri = "${properties.api.uri}/user/{id}"

        /**
         * This property represents a logger instance for logging messages. It is
         * used for logging messages related to the current class.
         */
        protected open val logger: Logger = Logger.getLogger(this.javaClass.name)

        /**
         * Retrieves the profile of a user identified by the given ID.
         *
         * @param id The ID of the profile (phone number, email, or ID)
         * @return The profile information of the user as an [IdentityDomainAccount] object.
         * @throws IdentityGetAccountException If an error occurs while retrieving the profile.
         */
        override fun getProfile(id: String): IdentityDomainAccount {
            try {
                return webClient
                    .get()
                    .uri(getUserInfoUri.replace("{id}", URLPreparation.encode(id, Charset.defaultCharset())))
                    .header("Authorization", "Bearer ${holder.getAccessToken()}")
                    .header("x-api-version", properties.api.apiVersion)
                    .parseExceptionAndExchange { _, clientResponse ->
                        val body = clientResponse.bodyTo(String::class.java)
                        if (clientResponse.statusCode.is2xxSuccessful) {
                            val account = mappingHelper.readValue(body, IdentityAccount::class.java)
                            return@parseExceptionAndExchange account
                        } else {
                            throw IdentityGetAccountException(clientResponse.statusCode.value(), body)
                        }
                    }.toDomain()
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw if (t is IdentityException) t else IdentityGetAccountException(exception = t)
            }
        }
    }
}
