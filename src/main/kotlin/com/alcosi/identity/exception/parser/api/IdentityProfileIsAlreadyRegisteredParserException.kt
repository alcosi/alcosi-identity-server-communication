package com.alcosi.identity.exception.parser.api

import com.alcosi.identity.service.error.IdentityParserApiErrorType

open class IdentityProfileIsAlreadyRegisteredParserException(originalMessage: String?, status: Int) : IdentityParserApiException(IdentityParserApiErrorType.PROFILE_IS_ALREADY_REGISTERED,"Profile is already registered",originalMessage,status)