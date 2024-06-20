package com.alcosi.identity.exception.api

import com.alcosi.identity.exception.IdentityException

open class IdentityRevokeException(statusCode: Int? = null, body: String? = null, exception: Throwable? = null) :
    IdentityException(statusCode, "Exception during revoke on IDServer. Can't revoke token", exception, body)