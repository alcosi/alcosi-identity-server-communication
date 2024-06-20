package com.alcosi.identity.exception.api

import com.alcosi.identity.exception.IdentityException

open class IdentityResetPasswordGetCodeException(statusCode: Int? = null, body: String? = null, exception: Throwable? = null) : IdentityException(statusCode, "Exception during reset on IDServer.Can't get reset code", exception, body)
