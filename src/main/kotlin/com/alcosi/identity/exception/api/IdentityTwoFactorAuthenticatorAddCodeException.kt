package com.alcosi.identity.exception.api

import com.alcosi.identity.exception.IdentityException

open class IdentityTwoFactorAuthenticatorAddCodeException(statusCode: Int? = null, body: String? = null, exception: Throwable? = null) :
        IdentityException(statusCode, "Exception 2fa on IDServer. Can not add Authenticator code", exception, body)