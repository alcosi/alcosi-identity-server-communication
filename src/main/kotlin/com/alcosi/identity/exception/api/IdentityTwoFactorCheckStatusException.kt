package com.alcosi.identity.exception.api

import com.alcosi.identity.exception.IdentityException

open class IdentityTwoFactorCheckStatusException(statusCode: Int? = null, body: String? = null, exception: Throwable? = null) :
        IdentityException(statusCode, "Exception 2fa on IDServer. Can't get 2fa status.", exception, body)
