package com.alcosi.identity.exception.api

import com.alcosi.identity.exception.IdentityException

open class IdentityChangeClaimException(statusCode: Int? = null, body: String? = null, exception: Throwable? = null) :
    IdentityException(statusCode, "Exception during claims change. Can't change claims", exception, body)
