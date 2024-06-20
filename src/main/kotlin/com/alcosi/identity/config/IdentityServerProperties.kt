package com.alcosi.identity.config

import io.github.breninsul.rest.logging.RestTemplateLoggerProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * Represents the configuration properties for the Identity Server.
 *
 * @property enabled Indicates whether the Identity Server is enabled or not. Default is true.
 * @property api The API configuration for the Identity Server.
 * @property ids The configuration properties for Ids in Identity Server.
 * @property logging The logging configuration properties for the Identity Server.
 * @property connectTimeout The connection timeout duration.
 * @property readTimeout The read timeout duration.
 */
@ConfigurationProperties(prefix = "identity-service")
open class IdentityServerProperties(
    var enabled: Boolean = true,
    var api: Api = Api(),
    var ids: Ids = Ids(),
    var logging: RestTemplateLoggerProperties=RestTemplateLoggerProperties(),
    var connectTimeout: Duration = Duration.ofSeconds(10),
    var readTimeout: Duration = Duration.ofSeconds(120),
    var serviceIp:String="127.0.0.1"
) {
    /**
     * Represents the API configuration for the Identity Server.
     *
     * @property uri The URI of the Identity Server.
     * @property apiVersion The version of the API.
     */
    open class Api(
        var uri: String = "",
        val apiVersion:String="2.0"
        )

    /**
     * Represents the configuration properties for the Identity Server.
     *
     * @property uri The URI of the Identity Server.
     * @property ipHeader The header used to retrieve the client's IP address. Default is "X-Forwarded-For".
     * @property userAgentHeader The header used to retrieve the User-Agent information. Default is "User-Agent".
     * @property introspectionClient The client configuration for introspecting tokens.
     * @property apiClient The client configuration for making API requests.
     */
    open class Ids(
        var uri: String = "",
        var ipHeader: String = "X-Forwarded-For",
        var userAgentHeader: String = "User-Agent",
        var introspectionClient:Client=Client(),
        var apiClient:Client=Client(),
        )

    /**
     * Represents a client configuration for the Identity Server.
     *
     * @param id The client ID.
     * @param scope The scope of the client.
     * @param secret The client secret.
     * @param grantType The grant type of the client. Default is "client_credentials".
     */
    open class Client(
        var id: String = "",
        var scope: String = "",
        var secret: String = "",
        var grantType: String = "client_credentials",
    )
}