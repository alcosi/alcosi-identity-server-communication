package com.alcosi.identity.config

import java.net.URLEncoder
import java.nio.charset.Charset

object URLPreparation {
    private const val USE_URI_ENCODER:Boolean=false
    fun encode(value:String, charset: Charset =Charset.defaultCharset()):String{
        return  if (USE_URI_ENCODER)URLEncoder.encode(value, charset) else value
    }
}