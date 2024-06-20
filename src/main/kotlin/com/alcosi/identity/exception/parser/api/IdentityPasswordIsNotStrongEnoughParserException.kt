package com.alcosi.identity.exception.parser.api

import com.alcosi.identity.service.error.IdentityParserApiErrorType

open class IdentityPasswordIsNotStrongEnoughParserException(originalMessage: String?, status: Int) : IdentityParserApiException(IdentityParserApiErrorType.PASSWORD_IS_NOT_STRONG_ENOUGH,"Password is not strong enough",originalMessage,status)