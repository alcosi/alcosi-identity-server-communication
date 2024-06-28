package com.alcosi.identity.service.api

import com.alcosi.identity.config.IdentityServerProperties
import com.alcosi.identity.dto.api.IdentityResetCode
import com.alcosi.identity.dto.domain.IdentityDomainResetCode
import com.alcosi.identity.dto.domain.toDomain
import com.alcosi.identity.exception.IdentityException
import com.alcosi.identity.exception.api.IdentityResetPasswordGetCodeException
import com.alcosi.identity.exception.api.IdentityResetPasswordResetCodeException
import com.alcosi.identity.service.error.parseExceptionAndExchange
import com.alcosi.identity.service.token.IdentityClientTokenHolder
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.breninsul.rest.logging.RestTemplateConfigHeaders
import org.springframework.web.client.RestClient
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Represents a component for resetting passwords on the Identity Server.
 */
interface IdentityResetPasswordComponent {
    /**
     * Retrieves the Identity Domain Reset Code used for resetting a profile's password.
     *
     * @param id The profile's phone number, email, or ID.
     * @return The Identity Domain Reset Code.
     */
    fun getCode(id: String): IdentityDomainResetCode

    /**
     * Resets the code for a user's profile.
     *
     * @param id The profile's phone number, email, or ID.
     * @param code The code associated with the reset code. Can be null.
     * @param token The token used for authorization. Can be null.
     * @param password The user's new password.
     */
    fun resetCode(
        id: String,
        code: String?,
        token: String?,
        password: String,
    )

    /**
     * This class represents a component for resetting passwords on the
     * Identity Server.
     *
     * @property tokenHolder The token holder for the client.
     * @property properties The properties for the Identity Server's API.
     * @property mappingHelper The object mapper used for mapping JSON to
     *     objects.
     * @property restClient The REST client used for making requests to the
     *     Identity Server. (Default: RestClient.create())
     * @property uriSend The URI used for sending a password reset request.
     * @property uriReset The URI used for resetting a user's password.
     * @property logger The logger instance for logging messages related to the
     *     class.
     */
    open class Implementation(
        protected open val tokenHolder: IdentityClientTokenHolder,
        protected open val properties: IdentityServerProperties,
        protected open val mappingHelper: ObjectMapper,
        protected open val restClient: RestClient,
    ) : IdentityResetPasswordComponent {
        /** The URI used for sending a password reset request. */
        protected open val uriSend = "${properties.api.uri}/user/{emailOrPhoneOrId}/password/forgot"

        /** The URI used for resetting a user's password. */
        protected open val uriReset = "${properties.api.uri}/user/{emailOrPhoneOrId}/password/reset"

        /**
         * This property represents a logger instance for logging messages. It is
         * used for logging messages related to the current class.
         */
        protected open val logger: Logger = Logger.getLogger(this.javaClass.name)

        /**
         * Retrieves an IdentityDomainResetCode object for the specified ID.
         *
         * @param id The ID of the profile, can be an email, phone number, or ID.
         * @return An instance of IdentityDomainResetCode.
         * @throws IdentityResetPasswordGetCodeException if unable to retrieve the reset code from the Identity Server.
         */
        override fun getCode(id: String): IdentityDomainResetCode {
            try {
                return restClient
                    .get()
                    .uri(uriSend.replace("{emailOrPhoneOrId}", URLEncoder.encode(id, Charset.defaultCharset())))
                    .header("Authorization", "Bearer ${tokenHolder.getAccessToken()}")
                    .header("x-api-version", properties.api.apiVersion)
                    .headers { if (properties.disableBodyLoggingWithCode) it.set(RestTemplateConfigHeaders.LOG_RESPONSE_BODY,"false") }
                    .exchange { _, clientResponse ->
                        val body = clientResponse.bodyTo(String::class.java)
                        if (clientResponse.statusCode.is2xxSuccessful) {
                            return@exchange mappingHelper.readValue(body, IdentityResetCode::class.java)!!
                        } else {
                            throw IdentityResetPasswordGetCodeException(clientResponse.statusCode.value(), body)
                        }
                    }.toDomain()
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw if (t is IdentityException) t else IdentityResetPasswordGetCodeException(exception = t)
            }
        }

        /**
         * Resets the code for a given profile ID.
         *
         * @param id The ID of the profile, which can be an email, phone number, or ID.
         * @param code The code to reset, can be null.
         * @param token The token associated with the profile, can be null.
         * @param password The new password for the profile.
         * @throws IdentityResetPasswordResetCodeException if an exception occurs during the reset process.
         */
        override fun resetCode(
            id: String,
            code: String?,
            token: String?,
            password: String,
        ) {
            try {
                restClient
                    .post()
                    .uri(uriReset.replace("{emailOrPhoneOrId}", URLEncoder.encode(id, Charset.defaultCharset())))
                    .header("Authorization", "Bearer ${tokenHolder.getAccessToken()}")
                    .header("x-api-version", properties.api.apiVersion)
                    .headers { if (properties.disableBodyLoggingWithCode) it.set(RestTemplateConfigHeaders.LOG_REQUEST_BODY,"false") }
                    .headers { if (properties.disableBodyLoggingWithPassword) it.set(RestTemplateConfigHeaders.LOG_REQUEST_BODY,"false") }
                    .body(IdentityResetCode(code, token, password))
                    .parseExceptionAndExchange { _, clientResponse ->
                        val body = clientResponse.bodyTo(String::class.java)
                        if (clientResponse.statusCode.is2xxSuccessful) {
                            return@parseExceptionAndExchange
                        } else {
                            throw IdentityResetPasswordResetCodeException(clientResponse.statusCode.value(), body)
                        }
                    }
            } catch (t: Throwable) {
                val exception = if (t is IdentityException) t else IdentityResetPasswordResetCodeException(exception = t)
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw exception
            }
        }
    }
}
