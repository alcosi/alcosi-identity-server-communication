package com.alcosi.identity.exception.parser.ids

import com.alcosi.identity.service.error.IdentityParserIdsErrorType

open class IdentityInvalidTokenParserException(originalMessage: String?, status: Int) : IdentityParserIdsException(IdentityParserIdsErrorType.INVALID_CREDENTIALS,"Exception {action}. Token is invalid",originalMessage,status)