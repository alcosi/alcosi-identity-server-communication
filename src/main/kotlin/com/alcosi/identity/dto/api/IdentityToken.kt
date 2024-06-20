package com.alcosi.identity.dto.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.NullNode

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
open class IdentityToken
    @JsonCreator
    constructor(
        @JsonProperty("access_token")
        val accessToken: String,
        @JsonProperty("token_type")
        val tokenType: String,
        @JsonProperty("refresh_token")
        val refreshToken: String?,
        @JsonProperty("expires_in")
        val expiresIn: Int,
        @JsonProperty("scope")
        @JsonDeserialize(using = ListStringDeSerializer::class)
        val scopes: List<String>,
    ) {
        open class ListStringDeSerializer : StdDeserializer<List<String>>(List::class.java) {
            override fun deserialize(
                p: JsonParser?,
                ctxt: DeserializationContext?,
            ): List<String> {
                if (p == null) {
                    return listOf()
                }
                val jsonNode = ctxt!!.readTree(p)
                return if (jsonNode is NullNode) {
                    listOf()
                } else {
                    return jsonNode.textValue().split(" ")
                }
            }
        }
    }
