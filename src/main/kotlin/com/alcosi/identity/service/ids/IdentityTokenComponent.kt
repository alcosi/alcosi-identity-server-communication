package com.alcosi.identity.service.ids

import com.alcosi.identity.config.IdentityServerProperties
import com.alcosi.identity.dto.api.IdentityToken
import com.alcosi.identity.dto.domain.IdentityDomainToken
import com.alcosi.identity.exception.IdentityException
import com.alcosi.identity.exception.ids.IdentityUnknownTokenException
import com.alcosi.identity.service.error.parseExceptionAndExchange
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.breninsul.rest.logging.RestTemplateConfigHeaders
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestClient
import java.time.LocalDateTime
import java.util.logging.Level
import java.util.logging.Logger

/**
 * The `IdentityTokenComponent` interface represents a component responsible for retrieving an access token from the server.
 * It defines the `getFromServer` method, which allows clients to retrieve a token using various parameters.
 */
interface IdentityTokenComponent {
    /**
     * Retrieves a token from the server.
     *
     * @param clientId The client ID used for authentication.
     * @param clientSecret The client secret used for authentication.
     * @param scopes The scopes to be requested.
     * @param grantType The grant type for the token request. Default value is
     *     "client_credentials".
     * @param username The username for the token request.
     * @param password The password for the token request.
     * @param code The code for the token request.
     * @param refreshToken The refresh token for the token request.
     * @param ip The IP address of the client making the request.
     * @param userAgent The User-Agent header for the request.
     * @return The retrieved DomainToken.
     * @throws IdentityException if an error occurs during the token retrieval
     *     process.
     */
    fun getFromServer(
        clientId: String,
        clientSecret: String,
        scopes: List<String>,
        grantType: String = "client_credentials",
        username: String? = null,
        password: String? = null,
        code: String? = null,
        refreshToken: String? = null,
        ip: String,
        userAgent: String?,
    ): IdentityDomainToken

    /**
     * The `IdentityTokenComponent` class is responsible for retrieving an access token from the server.
     * It utilizes the specified mappingHelper, properties, and restClient to interact with the server.
     *
     * @property mappingHelper The ObjectMapper used to map JSON responses to `IdentityToken` objects.
     * @property properties The properties of the Identity Server, including the API URI, IP header, and User-Agent header.
     * @property restClient The RestClient used to make HTTP requests to the server. It is optional and will be initialized
     * with a default RestClient if not provided.
     * @property relativePath The relative path to the token endpoint on the server. The default value is*/
    open class Implementation(
        protected open val mappingHelper: ObjectMapper,
        protected open val properties: IdentityServerProperties,
        protected open val restClient: RestClient ,
        relativePath: String = "/connect/token",
    ) : IdentityTokenComponent {
        /**
         * Represents the URI for the token.
         */
        protected open val tokenUri = "${properties.ids.uri}$relativePath"

        /**
         * This property represents a logger instance for logging messages. It is
         * used for logging messages related to the current class.
         */
        protected open val logger: Logger = Logger.getLogger(this.javaClass.name)

        /**
         * Retrieves a token from the server.
         *
         * @param clientId The client ID used for authentication.
         * @param clientSecret The client secret used for authentication.
         * @param scopes The scopes to be requested.
         * @param grantType The grant type for the token request. Default value is
         *     "client_credentials".
         * @param username The username for the token request.
         * @param password The password for the token request.
         * @param code The code for the token request.
         * @param refreshToken The refresh token for the token request.
         * @param ip The IP address of the client making the request.
         * @param userAgent The User-Agent header for the request.
         * @return The retrieved DomainToken.
         * @throws IdentityException if an error occurs during the token retrieval
         *     process.
         */
        override fun getFromServer(
            clientId: String,
            clientSecret: String,
            scopes: List<String>,
            grantType: String,
            username: String?,
            password: String?,
            code: String?,
            refreshToken: String?,
            ip: String,
            userAgent: String?,
        ): IdentityDomainToken {
            val formData: MultiValueMap<String, String> = LinkedMultiValueMap()
            listOf(
                "client_id" to clientId,
                "client_secret" to clientSecret,
                "scope" to if (scopes.isEmpty()) null else scopes.joinToString(" "),
                "grant_type" to grantType,
                "username" to username,
                "password" to password,
                "code" to code,
                "refresh_token" to refreshToken,
            ).filter { it.second != null }
                .forEach { p -> formData.add(p.first, p.second) }
            val rqTime = LocalDateTime.now()
            try {
                val requestSpec =
                    restClient
                        .post()
                        .uri(tokenUri)
                        .headers { if (properties.disableBodyLoggingWithPassword) it.set(RestTemplateConfigHeaders.LOG_REQUEST_BODY,"false") }
                        .headers { if (properties.disableBodyLoggingWithCode) it.set(RestTemplateConfigHeaders.LOG_REQUEST_BODY,"false") }
                        .headers { if (properties.disableBodyLoggingWithToken) it.set(RestTemplateConfigHeaders.LOG_RESPONSE_BODY,"false") }
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .setHeaders(
                            ip,
                            userAgent,
                        )
                return requestSpec
                    .body(formData)
                    .parseExceptionAndExchange { _, clientResponse ->
                        val rsString = clientResponse.bodyTo(String::class.java)
                        if (clientResponse.statusCode.isError || rsString.isNullOrBlank()) {
                            throw IdentityUnknownTokenException(clientResponse.statusCode.value(), rsString)
                        }
                        mappingHelper
                            .readValue(rsString, IdentityToken::class.java)
                            .let { IdentityDomainToken(it.accessToken, it.refreshToken, rqTime, it.expiresIn, it.scopes) }
                    }
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw if (t is IdentityException) t else IdentityUnknownTokenException(exception = t)
            }
        }

        /**
         * Sets the headers for the request.
         *
         * @param ip The IP address of the client making the request.
         * @param userAgent The User-Agent header for the request.
         * @return The modified RestClient.RequestBodySpec.
         */
        protected open fun RestClient.RequestHeadersSpec<RestClient.RequestBodySpec>.setHeaders(
            ip: String,
            userAgent: String?,
        ): RestClient.RequestBodySpec {
            val ipSpec =
                this
                    .header(properties.ids.ipHeader, ip)
            return if (userAgent.isNullOrBlank()) {
                ipSpec
            } else {
                ipSpec
                    .header(properties.ids.userAgentHeader, userAgent)
            }
        }
    }
}
