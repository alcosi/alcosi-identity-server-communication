package com.alcosi.identity.exception.ids

import com.alcosi.identity.exception.IdentityException

open class IdentityExpiredOrInvalidTokenException(statusCode: Int, body: String?) :
    IdentityException(statusCode, "Token is expired or invalid", null, body)