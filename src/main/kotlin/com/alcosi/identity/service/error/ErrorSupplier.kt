package com.alcosi.identity.service.error

import org.springframework.web.client.RestClient

/**
 * Represents a supplier of error instances.
 */
fun interface ErrorSupplier {
    fun create(response: RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse): Throwable?
}
