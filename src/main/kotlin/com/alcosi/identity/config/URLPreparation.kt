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