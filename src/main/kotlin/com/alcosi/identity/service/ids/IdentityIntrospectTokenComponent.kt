package com.alcosi.identity.service.ids

import com.alcosi.identity.config.IdentityServerProperties
import com.alcosi.identity.dto.api.IdentityIntrospectTokenRs
import com.alcosi.identity.dto.domain.IdentityDomainIntrospectedToken
import com.alcosi.identity.dto.domain.toDomain
import com.alcosi.identity.exception.IdentityException
import com.alcosi.identity.exception.ids.IdentityExpiredTokenException
import com.alcosi.identity.exception.ids.IdentityIntrospectTokenException
import com.alcosi.identity.exception.ids.IdentityInvalidTokenException
import com.alcosi.identity.service.error.parseExceptionAndExchange
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.breninsul.logging.HttpConfigHeaders
import org.apache.commons.codec.binary.Base64
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestClient
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Interface for the component responsible for introspecting identity tokens.
 */
interface IdentityIntrospectTokenComponent : IdentityProfileIdByTokenProvider {
    /**
     * Introspects the given token to retrieve information about the identity.
     *
     * @param token the token to be introspected
     * @return an instance of [IdentityDomainIntrospectedToken] containing the
     *     introspected token information
     * @throws IdentityInvalidTokenException if the token is invalid
     * @throws IdentityExpiredTokenException if the token is expired
     * @throws IdentityIntrospectTokenException if there is an exception during
     *     introspecting the token
     */
    fun introspect(token: String): IdentityDomainIntrospectedToken

    /**
     * This class is responsible for introspecting a token and retrieving
     * user information related to the token. It implements the
     * IdentityUserInfoByTokenProvider interface.
     *
     * @param mappingHelper The ObjectMapper instance used for mapping JSON
     *     responses to objects.
     * @param webClient The RestClient instance used for making HTTP requests.
     * @param properties The IdentityServerProperties.Ids instance containing
     *     the server properties.
     * @param relativePath The relative path used to compose the introspect
     *     URI.
     */
    open class Implementation(
        protected open val mappingHelper: ObjectMapper,
        protected open val properties: IdentityServerProperties,
        protected open val webClient: RestClient,
    ) : IdentityIntrospectTokenComponent {
        /**
         * The introspect URI composed by concatenating the `uri` property from the
         * `properties` object and `relativePath`. This URI is used to retrieve
         * user information by token in the `getUserId` function.
         */
        protected open val introspectUri = "${properties.ids.uri}/connect/introspect"

        /**
         * This property represents a logger instance for logging messages. It is
         * used for logging messages related to the current class.
         */
        protected open val logger: Logger = Logger.getLogger(this.javaClass.name)

        /**
         * Introspects the given token to retrieve information about the identity.
         *
         * @param token the token to be introspected
         * @return an instance of [IdentityDomainIntrospectedToken] containing the
         *     introspected token information
         * @throws IdentityInvalidTokenException if the token is invalid
         * @throws IdentityExpiredTokenException if the token is expired
         * @throws IdentityIntrospectTokenException if there is an exception during
         *     introspecting the token
         */
        override fun introspect(token: String): IdentityDomainIntrospectedToken {
            try {
                val formData: MultiValueMap<String, String> = LinkedMultiValueMap()
                formData.add("token", token)
                return webClient
                    .post()
                    .uri(introspectUri)
                    .header("Authorization", "Basic ${getBasicAuth()}")
                    .headers { if (properties.disableBodyLoggingWithToken) it.set(HttpConfigHeaders.LOG_REQUEST_BODY,"false") }
                    .body(formData)
                    .parseExceptionAndExchange { _, clientResponse ->
                        val body = clientResponse.bodyTo(String::class.java)
                        if (clientResponse.statusCode.value() == 401 || clientResponse.statusCode.value() == 400) {
                            throw IdentityInvalidTokenException(clientResponse.statusCode.value(), body)
                        } else if (clientResponse.statusCode.is2xxSuccessful) {
                            val account = body?.let { mappingHelper.readValue(it, IdentityIntrospectTokenRs::class.java) }
                            if (account?.active != true || account.sub.isNullOrBlank()) {
                                throw IdentityExpiredTokenException(clientResponse.statusCode.value(), body)
                            }
                            return@parseExceptionAndExchange account.toDomain()
                        } else {
                            throw IdentityIntrospectTokenException(clientResponse.statusCode.value(), body)
                        }
                    }
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw if (t is IdentityException) t else IdentityIntrospectTokenException(exception = t)
            }
        }

        /**
         * Retrieves the identity profile ID associated with a token.
         *
         * @param token The token used to retrieve the identity profile ID.
         * @return The identity profile ID associated with the provided token.
         * @throws IdentityInvalidTokenException if the token is invalid
         * @throws IdentityExpiredTokenException if the token is expired
         * @throws IdentityIntrospectTokenException if there is an exception during
         *     introspecting the token
         */
        override fun getIdentityProfileIdByToken(token: String): String = introspect(token).sub!!

        /**
         * Retrieves the basic authentication string used for making API requests.
         *
         * @return The basic authentication string.
         */
        protected open fun getBasicAuth(): String = Base64.encodeBase64String("${properties.ids.introspectionClient.id}:${properties.ids.introspectionClient.secret}".toByteArray())
    }
}
