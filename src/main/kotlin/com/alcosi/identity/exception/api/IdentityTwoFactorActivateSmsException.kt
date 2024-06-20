package com.alcosi.identity.exception.api

import com.alcosi.identity.exception.IdentityException

open class IdentityTwoFactorActivateSmsException(statusCode: Int? = null, body: String? = null, exception: Throwable? = null) :
        IdentityException(statusCode, "Exception 2fa on IDServer. Can't activate 2fa SMS", exception, body, )
