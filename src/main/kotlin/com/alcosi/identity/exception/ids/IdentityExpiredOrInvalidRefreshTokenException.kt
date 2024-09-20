package com.alcosi.identity.exception.ids

import com.alcosi.identity.exception.IdentityException

open class IdentityExpiredOrInvalidRefreshTokenException(statusCode: Int, body: String?) :
    IdentityException(statusCode, "Refresh token is expired or invalid", null, body)