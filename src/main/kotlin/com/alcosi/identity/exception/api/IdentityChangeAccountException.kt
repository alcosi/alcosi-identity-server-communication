package com.alcosi.identity.exception.api

import com.alcosi.identity.exception.IdentityException

open class IdentityChangeAccountException (httpCode:Int?=null,originalMessage:String?=null,exception: Throwable?=null):
    IdentityException(httpCode, "Exception during account/user change. Can't change account",exception,   originalMessage)