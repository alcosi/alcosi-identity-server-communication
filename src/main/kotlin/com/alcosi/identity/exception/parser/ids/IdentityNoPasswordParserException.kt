package com.alcosi.identity.exception.parser.ids

import com.alcosi.identity.service.error.IdentityParserIdsErrorType

open class IdentityNoPasswordParserException(originalMessage: String?, status: Int) : IdentityParserIdsException(IdentityParserIdsErrorType.NO_PASSWORD,"Profile password is not set",originalMessage,status)