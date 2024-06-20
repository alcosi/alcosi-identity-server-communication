package com.alcosi.identity.exception.parser.api

import com.alcosi.identity.service.error.IdentityParserApiErrorType

open class IdentityProfileNotExistOnIdentityParserException(originalMessage: String?, status: Int) : IdentityParserApiException(IdentityParserApiErrorType.PROFILE_NOT_EXIST,"Profile not exist on Identity Server",originalMessage,status)