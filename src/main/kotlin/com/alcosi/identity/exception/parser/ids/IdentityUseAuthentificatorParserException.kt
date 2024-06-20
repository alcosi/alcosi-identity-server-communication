package com.alcosi.identity.exception.parser.ids

import com.alcosi.identity.service.error.IdentityParserIdsErrorType

open class IdentityUseAuthentificatorParserException(originalMessage: String?, status: Int) : IdentityParserIdsException(IdentityParserIdsErrorType.AUTHENTICATOR,"Please use AUTHENTICATOR",originalMessage,status)