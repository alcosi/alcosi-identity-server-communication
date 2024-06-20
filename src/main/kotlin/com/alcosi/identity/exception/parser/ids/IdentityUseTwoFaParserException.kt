package com.alcosi.identity.exception.parser.ids

import com.alcosi.identity.service.error.IdentityParserIdsErrorType

open class IdentityUseTwoFaParserException(originalMessage: String?, status: Int) : IdentityParserIdsException(IdentityParserIdsErrorType.TWO_FA,"Please use 2FA",originalMessage,status)