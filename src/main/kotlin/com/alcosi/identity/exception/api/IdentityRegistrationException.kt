package com.alcosi.identity.exception.api

import com.alcosi.identity.exception.IdentityException

open class IdentityRegistrationException(statusCode: Int? = null, body: String? = null, exception: Throwable? = null) : IdentityException(statusCode, "Exception during registration on IDServer. Can't register user", exception, body)
