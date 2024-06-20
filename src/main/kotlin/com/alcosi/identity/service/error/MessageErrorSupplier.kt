package com.alcosi.identity.service.error

import org.springframework.web.client.RestClient

/**
 * Represents a supplier of error instances for messages.
 */
fun interface MessageErrorSupplier : ErrorSupplier {
    override fun create(response: RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse): Throwable? {
        return create(response.bodyTo(String::class.java), response.statusCode.value())
    }

    fun create(message: String?, statusCode: Int): Throwable?
}