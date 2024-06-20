package com.alcosi.identity.service.ids

/**
 * IdentityProfileIdByTokenProvider interface provides a method to retrieve the user profile ID associated with a token.
 */
interface IdentityProfileIdByTokenProvider {
    /**
     * Retrieves the user profile ID associated with a token.
     *
     * @param token The token used to retrieve the user profile ID.
     * @return The user profile ID associated with the provided token.
     */
    fun getIdentityProfileIdByToken(token: String): String
}