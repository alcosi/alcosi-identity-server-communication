package com.alcosi.identity.service.api

import com.alcosi.identity.config.IdentityServerProperties
import com.alcosi.identity.dto.api.IdentityActivationCode
import com.alcosi.identity.dto.api.IdentityChangeContactGetCodeRq
import com.alcosi.identity.dto.api.IdentityChangeContactValidateCodeRq
import com.alcosi.identity.dto.domain.IdentityDomainActivationToken
import com.alcosi.identity.dto.domain.toDomain
import com.alcosi.identity.exception.IdentityException
import com.alcosi.identity.exception.api.IdentityChangeProfileContactsException
import com.alcosi.identity.service.error.parseExceptionAndExchange
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.breninsul.rest.logging.RestTemplateConfigHeaders
import org.springframework.web.client.RestClient
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.logging.Level
import java.util.logging.Logger

/**
 * The IdentityChangeContactComponent interface provides methods for retrieving identity activation codes and
 * validating codes against an identity server.
 */
interface IdentityChangeContactComponent {
    /**
     * Enum class representing different operations for IdentityChangeContactGetCodeRq.
     *
     * @property rqType The corresponding operation of IdentityChangeContactGetCodeRq.
     */
    enum class Operation(
        val rqType: IdentityChangeContactGetCodeRq.Operation,
    ) {
        REPLACE(IdentityChangeContactGetCodeRq.Operation.REPLACE),
        ADD(IdentityChangeContactGetCodeRq.Operation.ADD),
        REMOVE(IdentityChangeContactGetCodeRq.Operation.REMOVE),
    }

    /**
     * Enum class representing the types of profile contacts.
     *
     * @property rqType The corresponding ContactType of the IdentityChange*/
    enum class ProfileContactTypeEnum(
        val rqType: IdentityChangeContactGetCodeRq.ContactType,
    ) {
        EMAIL(IdentityChangeContactGetCodeRq.ContactType.EMAIL),
        PHONE(IdentityChangeContactGetCodeRq.ContactType.PHONE),
    }

    /**
     * Retrieves the identity activation code for a given operation, value, and contact type.
     *
     * @param token The authorization token.
     * @param operation The operation to be performed.
     * @param value The value of the contact.
     * @param type The type of the profile contact.
     * @return The retrieved identity activation code.
     * @throws IdentityChangeProfileContactsException If an error occurs during the process of retrieving the activation code.
     */
     fun getCode(
        token: String,
        operation: Operation,
        value: String,
        type: ProfileContactTypeEnum,
    ): IdentityDomainActivationToken

    /**
     * Validates the provided code against the identity server.
     *
     * @param token The authorization token.
     * @param id The identifier of profile (id , or email, or phone).
     * @param code The code to validate.
     * @return True if the code is valid, false otherwise.
     * @throws IdentityChangeProfileContactsException If an error occurs during the process of validating the code.
     */
     fun validateCode(
        token: String,
        id: String,
        code: String,
    ): Boolean

    /**
     * Represents a component for changing contact information in an identity server.
     * @property properties The API properties of the identity server.
     * @property mappingHelper The object mapper used for mapping JSON objects.
     * @property restClient The REST client used for making HTTP requests.
     */
    open class Implementation(
        protected open val properties: IdentityServerProperties,
        protected open val mappingHelper: ObjectMapper,
        protected open val restClient: RestClient,
    ) : IdentityChangeContactComponent {
        /**
         * The URI for getting a profile code.
         */
        protected open val getCodeUri = "${properties.api.uri}$/profile/{type}"

        /**
         * URI for validating the code.
         */
        protected open val validateCodeUri = "${properties.api.uri}$/profile/emailorphone"

        /**
         * This property represents a logger instance for logging messages. It is
         * used for logging messages related to the current class.
         */
        protected open val logger: Logger = Logger.getLogger(this.javaClass.name)

        /**
         * Retrieves the identity activation code for a given operation, value, and contact type.
         *
         * @param token The authorization token.
         * @param operation The operation to be performed.
         * @param value The value of the contact.
         * @param type The type of the profile contact.
         * @return The retrieved identity activation code.
         * @throws IdentityChangeProfileContactsException If an error occurs during the process of retrieving the activation code.
         */
        override fun getCode(
            token: String,
            operation: Operation,
            value: String,
            type: ProfileContactTypeEnum,
        ): IdentityDomainActivationToken {
            val rq = listOf(IdentityChangeContactGetCodeRq(operation.rqType, value, type.rqType))
            try {
                val identityCode =
                    restClient
                        .patch()
                        .uri(getCodeUri.replace("{type}", URLEncoder.encode(type.rqType.uriPath, Charset.defaultCharset())))
                        .header("Authorization", "Bearer $token")
                        .header("x-api-version", properties.api.apiVersion)
                        .headers { if (properties.disableBodyLoggingWithToken) it.set(RestTemplateConfigHeaders.LOG_REQUEST_HEADERS,"false") }
                        .headers { if (properties.disableBodyLoggingWithCode) it.set(RestTemplateConfigHeaders.LOG_RESPONSE_BODY,"false") }
                        .body(rq)
                        .parseExceptionAndExchange { _, clientResponse ->
                            val body = clientResponse.bodyTo(String::class.java)
                            if (clientResponse.statusCode.is2xxSuccessful) {
                                return@parseExceptionAndExchange mappingHelper.readValue(body, IdentityActivationCode::class.java)
                            } else {
                                throw IdentityChangeProfileContactsException(clientResponse.statusCode.value(), body)
                            }
                        }
                return identityCode.toDomain()
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw if (t is IdentityException) t else IdentityChangeProfileContactsException(exception = t)
            }
        }

        /**
         * Validates the provided code against the identity server.
         *
         * @param token The authorization token.
         * @param id The identifier of profile (id , or email, or phone).
         * @param code The code to validate.
         * @return True if the code is valid, false otherwise.
         * @throws IdentityChangeProfileContactsException If an error occurs during the process of validating the code.
         */
        override fun validateCode(
            token: String,
            id: String,
            code: String,
        ): Boolean {
            val rq = IdentityChangeContactValidateCodeRq(id, code)
            try {
                return restClient
                    .put()
                    .uri(validateCodeUri)
                    .header("Authorization", "Bearer $token")
                    .header("x-api-version", properties.api.apiVersion)
                    .headers { if (properties.disableBodyLoggingWithToken) it.set(RestTemplateConfigHeaders.LOG_REQUEST_HEADERS,"false") }
                    .headers { if (properties.disableBodyLoggingWithCode) it.set(RestTemplateConfigHeaders.LOG_REQUEST_BODY,"false") }
                    .body(rq)
                    .parseExceptionAndExchange { _, clientResponse ->
                        if (clientResponse.statusCode.is2xxSuccessful) {
                            return@parseExceptionAndExchange true
                        } else {
                            val body = clientResponse.bodyTo(String::class.java)
                            throw IdentityChangeProfileContactsException(clientResponse.statusCode.value(), body)
                        }
                    }
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw if (t is IdentityException) t else IdentityChangeProfileContactsException(exception = t)
            }
        }
    }
}
