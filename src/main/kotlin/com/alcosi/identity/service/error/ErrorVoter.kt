package com.alcosi.identity.service.error

import org.springframework.web.client.RestClient

/**
 * Represents a functional interface for voting on whether an error should be handled.
 */
fun interface ErrorVoter {
    fun vote(response: RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse): Boolean
}