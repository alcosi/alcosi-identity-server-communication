package com.alcosi.identity.exception.parser.api

import com.alcosi.identity.service.error.IdentityParserApiErrorType

open class IdentityWrongActivationCodeParserException(originalMessage: String?, status: Int) : IdentityParserApiException(IdentityParserApiErrorType.WRONG_ACTIVATION_CODE,"Wrong reset/activation code/token",originalMessage,status)