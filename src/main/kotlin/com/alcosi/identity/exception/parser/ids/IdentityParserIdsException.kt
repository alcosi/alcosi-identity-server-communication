package com.alcosi.identity.exception.parser.ids

import com.alcosi.identity.exception.parser.IdentityParserException
import com.alcosi.identity.service.error.IdentityParserIdsErrorType

open class IdentityParserIdsException(val type: IdentityParserIdsErrorType, message: String?, originalMessage: String?, status: Int) : IdentityParserException(message, originalMessage, status)