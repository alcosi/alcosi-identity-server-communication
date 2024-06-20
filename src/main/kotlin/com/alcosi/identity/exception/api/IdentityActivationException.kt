package com.alcosi.identity.exception.api

import com.alcosi.identity.exception.IdentityException

abstract class IdentityActivationException( httpStatusCode:Int?=null,
                                        originalMessage:String?=null,
                                        message:String="Exception during activation on IDServer.",
                                        exception: Throwable? = null) :
        IdentityException(httpStatusCode, message,exception,originalMessage)
open class IdentityGetActivationCodeException(httpStatusCode:Int?=null, originalMessage:String?=null,exception: Throwable? = null):
    IdentityActivationException(httpStatusCode, originalMessage,"Exception during activation on IDServer. Can't get activation code", exception)

open class IdentityApproveActivationCodeException(httpStatusCode:Int?=null, originalMessage:String?=null,exception: Throwable? = null):
    IdentityActivationException(httpStatusCode, originalMessage,"Exception during activation on IDServer. Can't get approve code", exception)