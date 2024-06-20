package com.alcosi.identity.service.error

import org.springframework.web.client.RestClient

/**
 * Represents a functional interface for voting on whether an error message should be handled.
 *
 * This interface extends the ErrorVoter interface.
 */
fun interface MessageErrorVoter : ErrorVoter {
    override fun vote( response: RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse): Boolean {
        return if (response.statusCode.is2xxSuccessful) {
            false
        } else {
            vote( response.bodyTo(String::class.java), response.statusCode.value())
        }
    }

    fun vote( message: String?, statusCode: Int): Boolean
}