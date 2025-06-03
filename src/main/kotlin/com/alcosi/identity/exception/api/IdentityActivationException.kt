/*
 *
 *  * Copyright (c) 2025 Alcosi Group Ltd. and affiliates.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 */

package com.alcosi.identity.exception.api

import com.alcosi.identity.exception.IdentityException

abstract class IdentityActivationException( httpStatusCode:Int?=null,
                                        originalMessage:String?=null,
                                        message:String="Exception during activation on IDServer.",
                                        exception: Throwable? = null) :
        IdentityException(httpStatusCode, message,exception,originalMessage)
open class IdentityGetActivationCodeException(httpStatusCode:Int?=null, originalMessage:String?=null,exception: Throwable? = null):
    IdentityActivationException(httpStatusCode, originalMessage,"Exception during activation on IDServer. Can't get activation code", exception)

open class IdentityApproveActivationCodeException(httpStatusCode:Int?=null, originalMessage:String?=null,exception: Throwable? = null):
    IdentityActivationException(httpStatusCode, originalMessage,"Exception during activation on IDServer. Can't get approve code", exception)