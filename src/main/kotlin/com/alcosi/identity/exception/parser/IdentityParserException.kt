package com.alcosi.identity.exception.parser

import com.alcosi.identity.exception.IdentityException
import com.alcosi.identity.service.error.IdentityParserIdsErrorType

open class IdentityParserException(message: String?, originalMessage: String?, status: Int) : IdentityException(message = message, originalMessage = originalMessage, httpStatusCode = status)