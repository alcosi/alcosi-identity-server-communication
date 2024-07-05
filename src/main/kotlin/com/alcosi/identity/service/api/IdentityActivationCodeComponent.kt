package com.alcosi.identity.service.api

import com.alcosi.identity.config.IdentityServerProperties
import com.alcosi.identity.dto.domain.IdentityDomainActivationToken
import com.alcosi.identity.dto.api.IdentityActivationCode
import com.alcosi.identity.dto.domain.toDomain
import com.alcosi.identity.exception.api.IdentityApproveActivationCodeException
import com.alcosi.identity.exception.IdentityException
import com.alcosi.identity.exception.api.IdentityGetActivationCodeException
import com.alcosi.identity.service.error.parseExceptionAndExchange
import com.fasterxml.jackson.databind.ObjectMapper
import com.alcosi.identity.service.token.IdentityClientTokenHolder
import io.github.breninsul.rest.logging.RestTemplateConfigHeaders
import org.springframework.web.client.RestClient
import com.alcosi.identity.config.URLPreparation
import java.nio.charset.Charset
import java.util.logging.Level
import java.util.logging.Logger

/**
 * The IdentityActivationCodeComponent interface represents a component for retrieving and activating activation codes for user activation on the Identity Server.
 */
interface IdentityActivationCodeComponent {
    /**
     * Retrieves the activation code for the given ID.
     *
     * @param id The ID of the user (phone number, email, or ID)
     * @return The retrieved IdentityActivationCode object containing the code and token
     * @throws IdentityGetActivationCodeException if there is an exception during the retrieval of the activation code
     */
    fun getCode(id: String): IdentityDomainActivationToken

    /**
     * Activates the code for the given ID and token.
     *
     * @param id The ID of the profile (phone number, email, or ID)
     * @param code The activation code to be used (nullable)
     * @param token The token to authorize the activation (nullable)
     * @throws IdentityApproveActivationCodeException if the activation code is not approved
     * @throws IdentityException if there is an exception during activation on the Identity Server
     */
    fun activateCode(
        id: String,
        code: String?,
        token: String?,
    )


    /**
     * The IdentityActivationCodeComponent class is responsible for retrieving and activating activation codes
     * for user activation on the Identity Server.
     *
     * @param tokenHolder The ClientTokenHolder instance used for obtaining the access token for authorization
     * @param properties The IdentityServerProperties.Api instance containing the API configuration
     * @param mappingHelper The ObjectMapper instance used for mapping JSON responses to objects
     * @param restClient The RestClient instance used for making HTTP requests to the Identity Server
     */
    open class Implementation(
        protected open val tokenHolder: IdentityClientTokenHolder,
        protected open val properties: IdentityServerProperties,
        protected open val mappingHelper: ObjectMapper,
        protected open val restClient: RestClient,
    ) : IdentityActivationCodeComponent {
        /**
         * The URI for activating a user account.
         */
        protected open val uri = "${properties.api.uri}/user/{emailOrPhoneOrId}/activate"

        /**
         * This property represents a logger instance for logging messages.
         * It is used for logging messages related to the current class.
         */
        protected open val logger: Logger = Logger.getLogger(this.javaClass.name)


        /**
         * Retrieves the activation code for the given ID.
         *
         * @param id The ID of the user (phone number, email, or ID)
         * @return The retrieved IdentityActivationCode object containing the code and token
         * @throws IdentityGetActivationCodeException if there is an exception during the retrieval of the activation code
         */
        override fun getCode(id: String): IdentityDomainActivationToken {
            try {
                return restClient
                    .get()
                    .uri(uri, mapOf<String,String>("emailOrPhoneOrId" to id))
                    .header("Authorization", "Bearer ${tokenHolder.getAccessToken()}")
                    .header("x-api-version", properties.api.apiVersion)
                    .headers { if (properties.disableBodyLoggingWithCode) it.set(RestTemplateConfigHeaders.LOG_RESPONSE_BODY,"false") }
                    .parseExceptionAndExchange { _, clientResponse ->
                        val body = clientResponse.bodyTo(String::class.java)
                        if (clientResponse.statusCode.is2xxSuccessful) {
                            return@parseExceptionAndExchange mappingHelper.readValue(body, IdentityActivationCode::class.java)!!
                        } else {
                            throw IdentityGetActivationCodeException(clientResponse.statusCode.value(), body)
                        }
                    }.toDomain()
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw if (t is IdentityException) t else IdentityGetActivationCodeException(exception = t)
            }
        }

        /**
         * Activates the code for the given ID and token.
         *
         * @param id The ID of the profile (phone number, email, or ID)
         * @param code The activation code to be used (nullable)
         * @param token The token to authorize the activation (nullable)
         * @throws IdentityApproveActivationCodeException if the activation code is not approved
         * @throws IdentityException if there is an exception during activation on the Identity Server
         */
        override fun activateCode(
            id: String,
            code: String?,
            token: String?,
        ) {
            try {
                val response =
                    restClient
                        .post()
                        .uri(uri, mapOf<String,String>("emailOrPhoneOrId" to id))
                        .header("Authorization", "Bearer ${tokenHolder.getAccessToken()}")
                        .header("x-api-version", properties.api.apiVersion)
                        .headers { if (properties.disableBodyLoggingWithCode) it.set(RestTemplateConfigHeaders.LOG_REQUEST_BODY,"false") }
                        .body(IdentityActivationCode(code, token))
                        .exchange { _, clientResponse ->
                            val body = clientResponse.bodyTo(String::class.java)
                            if (clientResponse.statusCode.is2xxSuccessful) {
                                return@exchange
                            } else {
                                throw IdentityApproveActivationCodeException(clientResponse.statusCode.value(), body)
                            }
                        }
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw if (t is IdentityException) t else IdentityApproveActivationCodeException(exception = t)
            }
        }
    }
}