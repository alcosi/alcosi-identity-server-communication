package com.alcosi.identity.exception.api

import com.alcosi.identity.exception.IdentityException

open class IdentityGetAccountException(statusCode: Int? = null, body: String? = null, exception: Throwable? = null) : IdentityException(statusCode, "Exception during get userinfo by ID",exception, body,)
