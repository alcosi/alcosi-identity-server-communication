package com.alcosi.identity.dto.domain

import java.time.LocalDateTime

/**
 * Represents the Auth token class.
 *
 * @property accessToken the access token.
 * @property refreshToken the refresh token.
 * @property creationTime the creation time of the token.
 * @property expiresIn the expiration time of the token.
 * @property scopes the scopes of the token.
 * @property validTill the valid till time of the token.
 * @property success indicates whether the token retrieval was successful.
 * @property noSuccessReason the reason for the token retrieval failure.
 */
open class IdentityDomainToken(
    val accessToken: String,
    val refreshToken: String?,
    val creationTime: LocalDateTime,
    val expiresIn: Int,
    val scopes: List<String>,
    val validTill: LocalDateTime = creationTime.plusSeconds(expiresIn.toLong()),
    val success: Boolean = true,
    val noSuccessReason: NoSuccessReason? = null,
){
    enum class NoSuccessReason {
        USE_2FA,
        USE_AUTHENTICATOR,
        ADD_AUTHENTICATOR,
        NO_PASSWORD_IN_REQUEST,
        UNKNOWN,
    }

}
