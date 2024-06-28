package com.alcosi.identity.exception.ids

import com.alcosi.identity.exception.IdentityException

open class IdentityUnknownTokenException(statusCode: Int? = null, body: String? = null, exception: Throwable? = null) :
        IdentityException(statusCode, "Unknown exception during token creation $body", exception, body)