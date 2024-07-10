package com.alcosi.identity.service.api

import com.alcosi.identity.config.IdentityServerProperties
import com.alcosi.identity.dto.api.IdentityChangeClaimRq
import com.alcosi.identity.dto.api.IdentityServerClaim
import com.alcosi.identity.exception.IdentityException
import com.alcosi.identity.exception.api.IdentityChangeClaimException
import com.alcosi.identity.service.error.parseExceptionAndExchange
import com.alcosi.identity.service.token.IdentityClientTokenHolder
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.client.RestClient
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.logging.Level
import java.util.logging.Logger

/**
 * The IdentityClaimsComponent interface represents a component for changing identity claims.
 */
interface IdentityClaimsComponent {
    /**
     * Changes the claim value for the specified claimName and id.
     *
     * @param id The ID of profile (id,or phone,or email) to be changed.
     * @param claimName The name of the claim to be changed.
     * @param claimOldValue The old value of the claim. Optional, can be null.
     * @param claimValue The new value to be set for the claim.
     *
     * @return true if the claim was successfully changed, false otherwise.
     *
     * @throws IdentityChangeClaimException If there is an error while changing the claim.
     */
    fun changeClaim(
        id: String,
        claimName: String,
        claimOldValue: String?,
        claimValue: String,
    ): Boolean

    open class Implementation(
        protected open val tokenHolder: IdentityClientTokenHolder,
        protected open val properties: IdentityServerProperties,
        protected open val mappingHelper: ObjectMapper,
        protected open val restClient: RestClient,
    ) : IdentityClaimsComponent {
        /**
         * Protected open property uri represents the URI for making a request
         * to change a claim for a user. It is a string that is constructed
         * by concatenating the apiUri from properties with the path
         */
        protected open val uri = "${properties.api.uri}/user/{id}/claim"

        /**
         * This property represents a logger instance for logging messages. It is
         * used for logging messages related to the current class.
         */
        protected open val logger: Logger = Logger.getLogger(this.javaClass.name)

        /**
         * Changes the claim value for the specified claimName and id.
         *
         * @param id The ID of profile (id,or phone,or email) to be changed.
         * @param claimName The name of the claim to be changed.
         * @param claimOldValue The old value of the claim. Optional, can be null.
         * @param claimValue The new value to be set for the claim.
         *
         * @return true if the claim was successfully changed, false otherwise.
         *
         * @throws IdentityChangeClaimException If there is an error while changing the claim.
         */
        override fun changeClaim(
            id: String,
            claimName: String,
            claimOldValue: String?,
            claimValue: String,
        ): Boolean {
            try {
                val rq =
                    IdentityChangeClaimRq(
                        IdentityServerClaim(claimName, claimOldValue),
                        IdentityServerClaim(claimName, claimValue),
                    )
                return restClient
                    .put()
                    .uri(uri.replace("{id}", URLEncoder.encode(id, Charset.defaultCharset())))
                    .header("Authorization", "Bearer ${tokenHolder.getAccessToken()}")
                    .header("x-api-version", properties.api.apiVersion)
                    .body(rq)
                    .parseExceptionAndExchange() { _, clientResponse ->
                        if (clientResponse.statusCode.is2xxSuccessful) {
                            return@parseExceptionAndExchange true
                        } else {
                            val body = clientResponse.bodyTo(String::class.java)
                            throw IdentityChangeClaimException(clientResponse.statusCode.value(), body)
                        }
                    }
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Exception Identity server:", t)
                throw if (t is IdentityException) t else IdentityChangeClaimException(exception = t)
            }
        }
    }
}
