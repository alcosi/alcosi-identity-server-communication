package com.alcosi.identity.exception.ids

import com.alcosi.identity.exception.IdentityException

open class IdentityExpiredTokenException(
    statusCode: Int? = null,
    body: String? = null,
    exception: Throwable? = null,
) : IdentityException(statusCode, "Token is expired ", exception, body)
