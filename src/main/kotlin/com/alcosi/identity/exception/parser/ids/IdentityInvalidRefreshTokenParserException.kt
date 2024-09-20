package com.alcosi.identity.exception.parser.ids

import com.alcosi.identity.service.error.IdentityParserIdsErrorType

open class IdentityInvalidRefreshTokenParserException(originalMessage: String?, status: Int) : IdentityParserIdsException(IdentityParserIdsErrorType.INVALID_CREDENTIALS,"Exception {action}. Refresh token is invalid",originalMessage,status)