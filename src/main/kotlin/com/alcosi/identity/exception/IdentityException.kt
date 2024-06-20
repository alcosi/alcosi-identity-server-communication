package com.alcosi.identity.exception

open class IdentityException(val httpStatusCode:Int?,message: String? = null, ex: Throwable? = null,val originalMessage:String?=null) : RuntimeException(message, ex)