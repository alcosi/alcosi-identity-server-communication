package com.alcosi.identity.exception.parser.api

import com.alcosi.identity.service.error.IdentityParserApiErrorType

open class IdentityInvalidCodeParserException(originalMessage: String?, status: Int) : IdentityParserApiException(IdentityParserApiErrorType.INVALID_CODE,"Wrong reset/activation code/token",originalMessage,status)