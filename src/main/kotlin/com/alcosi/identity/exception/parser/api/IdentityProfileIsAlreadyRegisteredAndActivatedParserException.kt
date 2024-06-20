package com.alcosi.identity.exception.parser.api

import com.alcosi.identity.service.error.IdentityParserApiErrorType

open class IdentityProfileIsAlreadyRegisteredAndActivatedParserException(originalMessage: String?, status: Int) : IdentityParserApiException(IdentityParserApiErrorType.PROFILE_IS_ALREADY_REGISTERED,"Profile is already registered and activated",originalMessage,status)