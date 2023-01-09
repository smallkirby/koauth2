package com.github.smallkirby.koauth2

class AccessToken private constructor() {
    private val expiresIn: Int? = null
    private val expiresAt: Int? = null
    private val accessToken: String? = null
    private val refreshToken: String? = null
    private val scope: List<String> = mutableListOf()
    private val properties = mutableMapOf<String, String>()

    companion object {
        enum class PropertyName(val value: String) {
            EXPIRES_IN("expires_in"),
            EXPIRES_AT("expires_at"),
            ACCESS_TOKEN("access_token"),
            REFRESH_TOKEN("refresh_token"),
            SCOPE("scope")
        }
    }
}