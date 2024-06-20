package com.alcosi.identity.exception.parser.ids

import com.alcosi.identity.service.error.IdentityParserIdsErrorType

open class IdentityInvalidCredentialsParserException(originalMessage: String?, status: Int) : IdentityParserIdsException(IdentityParserIdsErrorType.INVALID_CREDENTIALS,"Account credentials are not valid",originalMessage,status)