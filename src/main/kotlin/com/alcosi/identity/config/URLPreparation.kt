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

package com.alcosi.identity.config

import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.logging.Level
import java.util.logging.Logger

object URLPreparation{
    private val logger: Logger = Logger.getLogger(this.javaClass.name)
    private const val USE_URI_ENCODER:Boolean=true
    private const val  LOG:Boolean=true
    fun encode(value:String, charset: Charset =Charsets.UTF_8):String{
        val encoded = if (USE_URI_ENCODER) URLEncoder.encode(value, charset) else value
        if (LOG) {
            logger.log(Level.INFO,"URL encoded $value->$encoded")
        }
        return encoded
    }
}