package com.alcosi.identity.service.api

import com.alcosi.identity.config.IdentityServerProperties
import com.alcosi.identity.dto.domain.IdentityDomainRegistration
import com.alcosi.identity.dto.domain.toApi
import com.alcosi.identity.exception.IdentityException
import com.alcosi.identity.exception.api.IdentityRegistrationException
import com.alcosi.identity.service.error.parseExceptionAndExchange
import com.alcosi.identity.service.token.IdentityClientTokenHolder
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.breninsul.rest.logging.RestTemplateConfigHeaders
import org.springframework.web.client.RestClient
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Represents a component used for identity registration.
 */
interface IdentityRegistrationComponent {
    /**
     * Registers a user in the Identity Server.
     *
     * @param rq The registration request containing user information.
     * @return True if the registration is successful, false otherwise.
     * @throws IdentityException if an exception occurs during registration.
     */
    fun register(rq: IdentityDomainRegistration): Boolean

    /**
     * This class represents a component used for identity registration.
     *
     * @param tokenHolder The client token holder.
     * @param properties The API properties for the identity server.
     * @param mappingHelper The object mapper for mapping JSON.
     * @param restClient The REST client for making API calls.
     */
    open class Implementation(
        protected open val tokenHolder: IdentityClientTokenHolder,
        protected open val properties: IdentityServerProperties,
        protected open val mappingHelper: ObjectMapper,
        protected open val restClient: RestClient,
    ) : IdentityRegistrationComponent {
        /** The URI for registering a user. */
        protected open val uri = "${properties.api.uri}/user/register"

        /**
         * This property represents a logger instance for logging messages. It is
         * used for logging messages related to the current class.
         */
        protected open val logger: Logger = Logger.getLogger(this.javaClass.name)

        /**
         * Registers a user in the Identity Server.
         *
         * @param rq The registration request containing user information.
         * @return True if the registration is successful, false otherwise.
         * @throws IdentityException if an exception occurs during registration.
         */
        override fun register(rq: IdentityDomainRegistration): Boolean {
            try {
                return restClient
                    .post()
                    .uri(uri)
                    .header("Authorization", "Bearer ${tokenHolder.getAccessToken()}")
                    .header("x-api-version", properties.api.apiVersion)
                    .headers { if (properties.disableBodyLoggingWithPassword) it.set(RestTemplateConfigHeaders.LOG_REQUEST_BODY,"false") }
                    .body(rq.toApi())
                    .parseExceptionAndExchange { _, clientResponse ->
                        if (clientResponse.statusCode.is2xxSuccessful) {
                            return@parseExceptionAndExchange true
                        } else {
                            val body = clientResponse.bodyTo(String::class.java)
                            throw IdentityRegistrationException(clientResponse.statusCode.value(), body)
                        }
                    }
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw if (t is IdentityException) t else IdentityRegistrationException(exception = t)
            }
        }
    }
}
