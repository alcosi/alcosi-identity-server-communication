package com.alcosi.identity.exception.api

import com.alcosi.identity.exception.IdentityException

open class IdentityTwoFactorAuthenticatorRecoveryCodeException(statusCode: Int? = null, body: String? = null, exception: Throwable? = null) :
        IdentityException(statusCode, "Exception 2fa on IDServer. Can not generate recovery codes for Authenticator", exception, body)