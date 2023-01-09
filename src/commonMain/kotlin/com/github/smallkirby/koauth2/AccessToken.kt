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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

typealias Seconds = Int

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
    val accessToken: String,
    @SerialName("token_type")
    val tokenType: TokenType,
    @SerialName("refresh_token")
    val refreshToken: String? = null,
    @SerialName("expires_in")
    val expiresIn: Seconds? = null,
    @SerialName("expires_at")
    @Serializable(with = DateSerializer::class)
    private var expiresAt: Date? = null,
    @SerialName("scope")
    private val scope: List<String> = mutableListOf(),
) {
    init {
        if (expiresIn != null && expiresAt == null) {
            expiresAt = Date().apply {
                time += expiresIn * 1000
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AccessToken) return false

        if (
            accessToken != other.accessToken ||
            tokenType != other.tokenType ||
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
        result = 31 * result + (expiresAt?.hashCode() ?: 0)
        result = 31 * result + (refreshToken?.hashCode() ?: 0)
        result = 31 * result + scope.hashCode()
        return result
    }

    fun expiresIn(expirationWindow: Seconds): Boolean? {
        val expiresAt = expiresAt ?: return null
        val now = (System.currentTimeMillis() / 1000).toInt()
        return expiresAt.time / 1000 - now < expirationWindow
    }

    fun expired() = expiresIn(0)

    companion object {
        private class DateSerializer : KSerializer<Date> {
            private val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

            override fun serialize(encoder: Encoder, value: Date) {
                encoder.encodeString(value.toString())
            }

            override fun deserialize(decoder: Decoder): Date {
                return formatter.parse(decoder.decodeString())
            }
        }

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
