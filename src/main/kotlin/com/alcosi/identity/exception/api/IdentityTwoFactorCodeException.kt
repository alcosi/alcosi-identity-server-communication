package com.alcosi.identity.exception.api

import com.alcosi.identity.exception.IdentityException

open class IdentityTwoFactorCodeException(statusCode: Int? = null, body: String? = null, exception: Throwable? = null) :
    IdentityException(statusCode, "Exception 2fa on IDServer. Can't get 2fa code", exception, body)
