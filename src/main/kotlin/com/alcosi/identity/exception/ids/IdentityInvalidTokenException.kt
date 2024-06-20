package com.alcosi.identity.exception.ids

import com.alcosi.identity.exception.IdentityException

open class IdentityInvalidTokenException(statusCode: Int? = null, body: String? = null, exception: Throwable? = null) :
        IdentityException(statusCode, "Token is invalid",exception, body)