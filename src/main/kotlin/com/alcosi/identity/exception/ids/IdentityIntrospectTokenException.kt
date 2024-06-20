package com.alcosi.identity.exception.ids

import com.alcosi.identity.exception.IdentityException

open class IdentityIntrospectTokenException(statusCode: Int? = null, body: String? = null, exception: Throwable? = null) :
        IdentityException(statusCode, "Exception during get userinfo by token", exception, body)
