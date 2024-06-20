package com.alcosi.identity.exception.parser.ids

import com.alcosi.identity.service.error.IdentityParserIdsErrorType

open class IdentityNotActivatedParserException(originalMessage: String?, status: Int) : IdentityParserIdsException(IdentityParserIdsErrorType.NOT_ACTIVATED,"Account is not activated",originalMessage,status)