package com.alcosi.identity.exception.api

import com.alcosi.identity.exception.IdentityException

open class IdentityDeleteException(statusCode: Int? = null, body: String? = null, exception: Throwable? = null) :
        IdentityException(statusCode, "Exception during delete on IDServer.Can't delete user/account",exception, body)
