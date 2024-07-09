package com.alcosi.identity.service.api

import com.alcosi.identity.config.IdentityServerProperties
import com.alcosi.identity.config.URLPreparation
import com.alcosi.identity.dto.api.Identity2FaAuthenticatorAddCodeRq
import com.alcosi.identity.dto.api.Identity2FaAuthenticatorGenerateCodeRs
import com.alcosi.identity.dto.api.Identity2FaCodeRs
import com.alcosi.identity.dto.api.Identity2FaRs
import com.alcosi.identity.dto.domain.IdentityDomainTOTPSharedKey
import com.alcosi.identity.dto.domain.IdentityDomainTwoFaCode
import com.alcosi.identity.dto.domain.IdentityDomainTwoFaStatus
import com.alcosi.identity.dto.domain.toDomain
import com.alcosi.identity.exception.IdentityException
import com.alcosi.identity.exception.api.*
import com.alcosi.identity.service.error.parseExceptionAndExchange
import com.alcosi.identity.service.token.IdentityClientTokenHolder
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.breninsul.logging.HttpConfigHeaders
import org.springframework.web.client.RestClient
import java.util.logging.Level
import java.util.logging.Logger

/**
 * The IdentityTwoFactorComponent class provides methods for managing two-factor authentication in an identity domain.
 *
 * @constructor Creates an instance of IdentityTwoFactorComponent.
 */
interface IdentityTwoFactorComponent {
    /**
     * Checks the status of the two-factor authentication for a given token.
     *
     * @param token The token to be used for authentication.
     * @return Returns the status of the two-factor authentication as an
     *     instance of IdentityDomainTwoFaStatus.
     * @throws IdentityTwoFactorCheckStatusException If an exception occurs
     *     while checking the status of the two-factor authentication.
     */
    fun checkStatus(token: String): IdentityDomainTwoFaStatus

    /**
     * Activates or deactivates two-factor authentication for a given token.
     *
     * @param token The token to be used for authentication.
     * @param enable Indicates whether to enable or disable two-factor
     *     authentication.
     * @throws IdentityTwoFactorActivateSmsException If an exception occurs
     *     while activating or deactivating two-factor authentication.
     */
    fun activate2Fa(
        token: String,
        enable: Boolean,
    )

    /**
     * Retrieves the two-factor authentication code for the given profile
     * contact.
     *
     * @param id The profile id (id,email or phone ) for which to retrieve the
     *     two-factor authentication code.
     * @return The two-factor authentication code as an instance of
     *     IdentityDomainTwoFaCode.
     * @throws IdentityTwoFactorCodeException If an exception occurs while
     *     retrieving the two-factor authentication code.
     */
    fun get2faCode(id: String): IdentityDomainTwoFaCode

    /**
     * Generates the two-factor authentication code (TOTP) for activating the
     * authenticator.
     *
     * @param token The token used for authentication.
     * @return The generated two-factor authentication code as an instance of
     *     IdentityDomainTOTPSharedKey.
     * @throws IdentityTwoFactorAuthenticatorGenerateCodeException If an
     *     exception occurs while generating the two-factor authentication
     *     code.
     */
    fun activateTOTPGenerateCode(token: String): IdentityDomainTOTPSharedKey

    /**
     * Activates the two-factor authentication code for the authenticator app.
     *
     * @param token The token to be used for authentication.
     * @param code The two-factor authentication code to be added.
     * @return Returns a list of strings containing the added codes.
     * @throws IdentityTwoFactorAuthenticatorAddCodeException if an exception occurs while adding the code.
     */
    fun activateTOTPAddCode(
        token: String,
        code: String,
    ): List<String>

    /**
     * Retrieves a list of recovery codes for the two-factor authenticator.
     *
     * @param token The token used for authentication.
     * @return A list of recovery codes as strings.
     * @throws IdentityTwoFactorAuthenticatorRecoveryCodeException If an exception occurs while retrieving the recovery codes.
     */
    fun recoveryTOTPCodes(token: String): List<String>

    /**
     * Removes the two-factor authentication codes for the specified token.
     *
     * @param token The token used for authentication.
     * @throws IdentityTwoFactorAuthenticatorRemoveCodeException If an exception occurs while removing the two-factor authentication codes.
     */
    fun removeTOTPCodes(token: String)

    /**
     * The IdentityTwoFactorComponent class represents a component for handling two-factor authentication in the Identity server.
     *
     * @property properties The API properties of the Identity server.
     * @property mappingHelper The object mapper used for mapping JSON responses.
     * @property tokenHolder The client token holder for accessing the Identity server.
     * @property restClient The REST client for making HTTP requests to the Identity server.
     */
    open class Implementation(
        protected open val tokenHolder: IdentityClientTokenHolder,
        protected open val properties: IdentityServerProperties,
        protected open val mappingHelper: ObjectMapper,
        protected open val restClient: RestClient,
    ) : IdentityTwoFactorComponent {
        /** Represents the URI for checking the status of two-factor authentication. */
        protected open val checkUri = "${properties.api.uri}/account/2fa"

        /**
         * The URI used to activate or deactivate two-factor authentication. It is
         * a protected open property.
         */
        protected open val activateUri = "${properties.api.uri}/account/2fa/enabled/{enable}"

        /** Represents the URI for retrieving the two-factor authentication code. */
        protected open val twoFaCodeUri = "${properties.api.uri}/user/{id}/2fa"

        /** Represents the URI for the authenticator endpoint. */
        protected open val authenticatorUri = "${properties.api.uri}/account/authenticator"

        /** The URI for generating recovery codes for two-factor authentication. */
        protected open val recoveryAuthenticatorCodesUri = "${properties.api.uri}/account/2fa/generaterecoverycodes"

        /**
         * This property represents a logger instance for logging messages. It is
         * used for logging messages related to the current class.
         */
        protected open val logger: Logger = Logger.getLogger(this.javaClass.name)

        /**
         * Checks the status of the two-factor authentication for a given token.
         *
         * @param token The token to be used for authentication.
         * @return Returns the status of the two-factor authentication as an
         *     instance of IdentityDomainTwoFaStatus.
         * @throws IdentityTwoFactorCheckStatusException If an exception occurs
         *     while checking the status of the two-factor authentication.
         */
        override fun checkStatus(token: String): IdentityDomainTwoFaStatus {
            try {
                return restClient
                    .get()
                    .uri(checkUri)
                    .header("Authorization", "Bearer $token")
                    .header("x-api-version", properties.api.apiVersion)
                    .headers { if (properties.disableBodyLoggingWithToken) it.set(HttpConfigHeaders.LOG_REQUEST_HEADERS,"false") }
                    .parseExceptionAndExchange { _, clientResponse ->
                        val body = clientResponse.bodyTo(String::class.java)
                        if (clientResponse.statusCode.is2xxSuccessful) {
                            return@parseExceptionAndExchange mappingHelper.readValue(body, Identity2FaRs::class.java)
                        } else {
                            throw IdentityTwoFactorCheckStatusException(clientResponse.statusCode.value(), body)
                        }
                    }.toDomain()
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw if (t is IdentityException) t else IdentityTwoFactorCheckStatusException(exception = t)
            }
        }

        /**
         * Activates or deactivates two-factor authentication for a given token.
         *
         * @param token The token to be used for authentication.
         * @param enable Indicates whether to enable or disable two-factor
         *     authentication.
         * @throws IdentityTwoFactorActivateSmsException If an exception occurs
         *     while activating or deactivating two-factor authentication.
         */
        override fun activate2Fa(
            token: String,
            enable: Boolean,
        ) {
            try {
                restClient
                    .put()
                    .uri(activateUri.replace("{enable}", enable.toString()))
                    .header("Authorization", "Bearer $token")
                    .header("x-api-version", properties.api.apiVersion)
                    .headers { if (properties.disableBodyLoggingWithToken) it.set(HttpConfigHeaders.LOG_REQUEST_HEADERS,"false") }
                    .parseExceptionAndExchange { _, clientResponse ->
                        val body = clientResponse.bodyTo(String::class.java)
                        if (clientResponse.statusCode.is2xxSuccessful) {
                            return@parseExceptionAndExchange
                        } else {
                            throw IdentityTwoFactorActivateSmsException(clientResponse.statusCode.value(), body)
                        }
                    }
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw if (t is IdentityException) t else IdentityTwoFactorActivateSmsException(exception = t)
            }
        }

        /**
         * Retrieves the two-factor authentication code for the given profile
         * contact.
         *
         * @param id The profile id (id,email or phone ) for which to retrieve the
         *     two-factor authentication code.
         * @return The two-factor authentication code as an instance of
         *     IdentityDomainTwoFaCode.
         * @throws IdentityTwoFactorCodeException If an exception occurs while
         *     retrieving the two-factor authentication code.
         */
        override fun get2faCode(id: String): IdentityDomainTwoFaCode {
            try {
                return restClient
                    .get()
                    .uri(twoFaCodeUri,mapOf<String,String>("id" to id))
                    .header("Authorization", "Bearer ${tokenHolder.getAccessToken()}")
                    .header("x-api-version", properties.api.apiVersion)
                    .headers { if (properties.disableBodyLoggingWithCode) it.set(HttpConfigHeaders.LOG_RESPONSE_BODY,"false") }
                    .parseExceptionAndExchange { _, clientResponse ->
                        val body = clientResponse.bodyTo(String::class.java)
                        if (clientResponse.statusCode.is2xxSuccessful) {
                            return@parseExceptionAndExchange mappingHelper.readValue(body, Identity2FaCodeRs::class.java)!!
                        } else {
                            throw IdentityTwoFactorCodeException(clientResponse.statusCode.value(), body)
                        }
                    }.toDomain()
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw if (t is IdentityException) t else IdentityTwoFactorCodeException(exception = t)
            }
        }

        /**
         * Generates the two-factor authentication code (TOTP) for activating the
         * authenticator.
         *
         * @param token The token used for authentication.
         * @return The generated two-factor authentication code as an instance of
         *     IdentityDomainTOTPSharedKey.
         * @throws IdentityTwoFactorAuthenticatorGenerateCodeException If an
         *     exception occurs while generating the two-factor authentication
         *     code.
         */
        override fun activateTOTPGenerateCode(token: String): IdentityDomainTOTPSharedKey {
            try {
                val code =
                    restClient
                        .get()
                        .uri(authenticatorUri)
                        .header("Authorization", "Bearer $token")
                        .header("x-api-version", properties.api.apiVersion)
                        .headers { if (properties.disableBodyLoggingWithToken) it.set(HttpConfigHeaders.LOG_REQUEST_HEADERS,"false") }
                        .headers { if (properties.disableBodyLoggingWithCode) it.set(HttpConfigHeaders.LOG_RESPONSE_BODY,"false") }
                        .parseExceptionAndExchange { _, clientResponse ->
                            val body = clientResponse.bodyTo(String::class.java)
                            if (clientResponse.statusCode.is2xxSuccessful) {
                                val rs = mappingHelper.readValue(body, Identity2FaAuthenticatorGenerateCodeRs::class.java)!!
                                return@parseExceptionAndExchange rs
                            } else {
                                throw IdentityTwoFactorAuthenticatorGenerateCodeException(clientResponse.statusCode.value(), body)
                            }
                        }
                return code.toDomain()
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw if (t is IdentityException) t else IdentityTwoFactorAuthenticatorGenerateCodeException(exception = t)
            }
        }

        /**
         * Activates the two-factor authentication code for the authenticator app.
         *
         * @param token The token to be used for authentication.
         * @param code The two-factor authentication code to be added.
         * @return Returns a list of strings containing the added codes.
         * @throws IdentityTwoFactorAuthenticatorAddCodeException if an exception occurs while adding the code.
         */
        override fun activateTOTPAddCode(
            token: String,
            code: String,
        ): List<String> {
            try {
                val code =
                    restClient
                        .post()
                        .uri(authenticatorUri)
                        .header("Authorization", "Bearer $token")
                        .header("x-api-version", properties.api.apiVersion)
                        .headers { if (properties.disableBodyLoggingWithToken) it.set(HttpConfigHeaders.LOG_REQUEST_HEADERS,"false") }
                        .headers { if (properties.disableBodyLoggingWithCode) it.set(HttpConfigHeaders.LOG_REQUEST_BODY,"false") }
                        .body(Identity2FaAuthenticatorAddCodeRq(code))
                        .parseExceptionAndExchange { _, clientResponse ->
                            val body = clientResponse.bodyTo(String::class.java)
                            if (clientResponse.statusCode.is2xxSuccessful) {
                                val codes = mappingHelper.readValue(body, String::class.java.arrayType()) as Array<String>
                                return@parseExceptionAndExchange codes.toList()
                            } else {
                                throw IdentityTwoFactorAuthenticatorAddCodeException(clientResponse.statusCode.value(), body)
                            }
                        }
                return code
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw if (t is IdentityException) t else IdentityTwoFactorAuthenticatorAddCodeException(exception = t)
            }
        }

        /**
         * Retrieves a list of recovery codes for the two-factor authenticator.
         *
         * @param token The token used for authentication.
         * @return A list of recovery codes as strings.
         * @throws IdentityTwoFactorAuthenticatorRecoveryCodeException If an exception occurs while retrieving the recovery codes.
         */
        override fun recoveryTOTPCodes(token: String): List<String> {
            try {
                val code =
                    restClient
                        .post()
                        .uri(recoveryAuthenticatorCodesUri)
                        .header("Authorization", "Bearer $token")
                        .header("x-api-version", properties.api.apiVersion)
                        .headers { if (properties.disableBodyLoggingWithToken) it.set(HttpConfigHeaders.LOG_REQUEST_HEADERS,"false") }
                        .headers { if (properties.disableBodyLoggingWithCode) it.set(HttpConfigHeaders.LOG_RESPONSE_BODY,"false") }
                        .parseExceptionAndExchange { _, clientResponse ->
                            val body = clientResponse.bodyTo(String::class.java)
                            if (clientResponse.statusCode.is2xxSuccessful) {
                                val codes = mappingHelper.readValue(body, String::class.java.arrayType()) as Array<String>
                                return@parseExceptionAndExchange codes.toList()
                            } else {
                                throw IdentityTwoFactorAuthenticatorRecoveryCodeException(clientResponse.statusCode.value(), body)
                            }
                        }
                return code
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw if (t is IdentityException) t else IdentityTwoFactorAuthenticatorRecoveryCodeException(exception = t)
            }
        }

        /**
         * Removes the two-factor authentication codes for the specified token.
         *
         * @param token The token used for authentication.
         * @throws IdentityTwoFactorAuthenticatorRemoveCodeException If an exception occurs while removing the two-factor authentication codes.
         */
        override fun removeTOTPCodes(token: String) {
            try {
                restClient
                    .delete()
                    .uri(activateUri)
                    .header("Authorization", "Bearer $token")
                    .header("x-api-version", properties.api.apiVersion)
                    .headers { if (properties.disableBodyLoggingWithToken) it.set(HttpConfigHeaders.LOG_REQUEST_HEADERS,"false") }
                    .parseExceptionAndExchange { _, clientResponse ->
                        val body = clientResponse.bodyTo(String::class.java)
                        if (clientResponse.statusCode.is2xxSuccessful) {
                            return@parseExceptionAndExchange
                        } else {
                            throw IdentityTwoFactorAuthenticatorRemoveCodeException(clientResponse.statusCode.value(), body)
                        }
                    }
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw if (t is IdentityException) t else IdentityTwoFactorAuthenticatorRemoveCodeException(exception = t)
            }
        }
    }
}
