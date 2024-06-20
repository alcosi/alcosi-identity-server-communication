package com.alcosi.identity.exception.parser.api

import com.alcosi.identity.service.error.IdentityParserApiErrorType

open class IdentityProfileIsAlreadyActivatedParserException(originalMessage: String?, status: Int) : IdentityParserApiException(IdentityParserApiErrorType.PROFILE_IS_ALREADY_ACTIVATED,"Profile is already activated",originalMessage,status)