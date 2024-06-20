package com.alcosi.identity.exception.api

import com.alcosi.identity.exception.IdentityException

open class IdentityChangeProfileContactsException(statusCode: Int? = null, originalMessage: String? = null, exception: Throwable? = null) :
        IdentityException(statusCode, "Exception during account/user contact change. Can't change account/user", exception,originalMessage)
