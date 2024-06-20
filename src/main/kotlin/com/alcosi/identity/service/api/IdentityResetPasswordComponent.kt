package com.alcosi.identity.service.api


import com.alcosi.identity.config.IdentityServerProperties
import com.alcosi.identity.dto.domain.IdentityDomainResetCode
import com.alcosi.identity.dto.api.IdentityResetCode
import com.alcosi.identity.dto.domain.toDomain
import com.alcosi.identity.exception.IdentityException
import com.alcosi.identity.exception.api.IdentityResetPasswordGetCodeException
import com.alcosi.identity.exception.api.IdentityResetPasswordResetCodeException
import com.alcosi.identity.service.error.parseExceptionAndExchange
import com.alcosi.identity.service.token.IdentityClientTokenHolder
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.client.RestClient
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.logging.Level
import java.util.logging.Logger

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
open class IdentityResetPasswordComponent(
    protected open val tokenHolder: IdentityClientTokenHolder,
    protected open val properties: IdentityServerProperties.Api,
    protected open val mappingHelper: ObjectMapper,
    protected open val restClient: RestClient,
) {
    /** The URI used for sending a password reset request. */
    protected open val uriSend = "${properties.uri}/user/{emailOrPhoneOrId}/password/forgot"

    /** The URI used for resetting a user's password. */
    protected open val uriReset = "${properties.uri}/user/{emailOrPhoneOrId}/password/reset"

    /**
     * This property represents a logger instance for logging messages. It is
     * used for logging messages related to the current class.
     */
    protected open val logger: Logger = Logger.getLogger(this.javaClass.name)


    /**
     * Fetches the reset code for the user.
     *
     * @param id The profile's phone number, email, or ID
     * @return IdentityDomainResetCode The user's reset code
     * @throws ApiException if unable to reset on the Identity Server
     */
    open fun getCode(id: String): IdentityDomainResetCode {
        try {
            return restClient
                .get()
                .uri(uriSend.replace("{emailOrPhoneOrId}", URLEncoder.encode(id, Charset.defaultCharset())))
                .header("Authorization", "Bearer ${tokenHolder.getAccessToken()}")
                .header("x-api-version", properties.apiVersion)
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
     * Approves reset code for the user.
     *
     * @param id The profile's phone number, email, or ID
     * @param code String The user's reset code
     * @param token String The user's reset token
     * @param password String The user's new password code
     * @throws ApiException if unable to reset on the Identity Server
     */
    open fun resetCode(
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
                .header("x-api-version", properties.apiVersion)
                .body(IdentityResetCode(code, token, password))
                .parseExceptionAndExchange() { _, clientResponse ->
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
