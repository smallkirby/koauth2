package com.github.smallkirby.koauth2

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

/**
 * Response for the access token request
 *
 * [AccessToken] represents not only Access Token itself,
 * but also other information in response for the access token request,
 * such as expiration time and refresh token.
 *
 * cf:
 *  - https://www.rfc-editor.org/rfc/rfc6749#section-5
 */
@Serializable
class AccessToken private constructor(
    @SerialName("access_token")
    private val accessToken: String,
    @SerialName("token_type")
    private val tokenType: TokenType,
    @SerialName("expires_in")
    private val expiresIn: Int? = null,
    @SerialName("expires_at")
    private val expiresAt: Int? = null,
    @SerialName("refresh_token")
    private val refreshToken: String? = null,
    @SerialName("scope")
    private val scope: List<String> = mutableListOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AccessToken) return false

        if (
            accessToken != other.accessToken ||
            tokenType != other.tokenType ||
            expiresIn != other.expiresIn ||
            expiresAt != other.expiresAt ||
            refreshToken != other.refreshToken ||
            scope != other.scope
        ) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = accessToken.hashCode()
        result = 31 * result + tokenType.hashCode()
        result = 31 * result + (expiresIn ?: 0)
        result = 31 * result + (expiresAt ?: 0)
        result = 31 * result + (refreshToken?.hashCode() ?: 0)
        result = 31 * result + scope.hashCode()
        return result
    }

    companion object {
        @Serializable(with = TokenTypeSerializer::class)
        enum class TokenType(val value: String) {
            BEARER("Bearer"),
            UNKNOWN("Unknown"),
        }

        private class TokenTypeSerializer : KSerializer<TokenType> {
            override val descriptor: SerialDescriptor by lazy {
                PrimitiveSerialDescriptor(
                    TokenType::class.qualifiedName!!,
                    PrimitiveKind.STRING,
                )
            }
            override fun deserialize(decoder: Decoder): TokenType {
                val value = decoder.decodeString()
                return TokenType.values().find { it.value.lowercase() == value.lowercase() } ?: TokenType.UNKNOWN
            }
            override fun serialize(encoder: Encoder, value: TokenType) = encoder.encodeString(value.value)
        }

        private val jsonBuilder = Json {
            ignoreUnknownKeys = true
        }

        fun fromJsonString(json: String): AccessToken {
            return jsonBuilder.decodeFromString(serializer(), json)
        }
    }
}
