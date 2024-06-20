package com.alcosi.identity.exception.parser.api

import com.alcosi.identity.service.error.IdentityParserApiErrorType

open class IdentityInvalidAuthentificatorCodeParserException(originalMessage: String?, status: Int) : IdentityParserApiException(IdentityParserApiErrorType.AUTHENTIFICATOR_CODE_IS_INVALID,"AUTHENTICATOR code is invalid",originalMessage,status)