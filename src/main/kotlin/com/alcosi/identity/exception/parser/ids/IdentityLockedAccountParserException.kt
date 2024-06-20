package com.alcosi.identity.exception.parser.ids

import com.alcosi.identity.service.error.IdentityParserIdsErrorType

open class IdentityLockedAccountParserException(originalMessage: String?, status: Int) : IdentityParserIdsException(IdentityParserIdsErrorType.LOCKED," Account is LOCKED",originalMessage,status)