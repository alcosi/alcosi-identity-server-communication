package com.alcosi.identity.exception.parser.ids

import com.alcosi.identity.service.error.IdentityParserIdsErrorType

open class IdentityInvalidTwoFaParserException(originalMessage: String?, status: Int) : IdentityParserIdsException(IdentityParserIdsErrorType.TWO_FA,"2FA or AUTHENTICATOR code is invalid",originalMessage,status)