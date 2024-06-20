package com.alcosi.identity.exception.parser.api

import com.alcosi.identity.exception.parser.IdentityParserException
import com.alcosi.identity.service.error.IdentityParserApiErrorType

open class IdentityParserApiException(val type: IdentityParserApiErrorType, message: String?, originalMessage: String?, status: Int) : IdentityParserException( message, originalMessage, status)