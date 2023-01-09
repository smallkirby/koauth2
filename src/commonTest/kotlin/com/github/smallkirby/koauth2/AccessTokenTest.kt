package com.github.smallkirby.koauth2

import kotlin.test.Test
import kotlin.test.assertEquals

class AccessTokenTest {
    /**
     * Create [AccessToken] using private constructor
     */
    private fun getAccessToken(
        accessToken: String,
        tokenType: AccessToken.Companion.TokenType,
        expiresIn: Int? = null,
        expiresAt: Int? = null,
        refreshToken: String? = null,
        scope: List<String> = mutableListOf(),
    ): AccessToken {
        val constructor = AccessToken::class.java.declaredConstructors.find {
            it.parameters.size == 6
        }
        require(constructor != null) {
            "Cannot find constructor for AccessToken"
        }
        constructor.isAccessible = true

        return constructor.newInstance(
            accessToken,
            tokenType,
            expiresIn,
            expiresAt,
            refreshToken,
            scope,
        ) as AccessToken
    }

    @Test
    fun testAccessTokenFromJson() {
        val responseJsonString = """
            {
               "access_token":"2YotnFZFEjr1zCsicMWpAA",
               "token_type":"Bearer",
               "expires_in":3600,
               "refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA",
               "example_parameter":"example_value"
            }
        """.trimIndent()

        val accessToken = AccessToken.fromJsonString(responseJsonString)
        assertEquals(
            getAccessToken(
                accessToken = "2YotnFZFEjr1zCsicMWpAA",
                tokenType = AccessToken.Companion.TokenType.BEARER,
                expiresIn = 3600,
                refreshToken = "tGzv3JOkF0XG5Qx2TlKWIA",
            ),
            accessToken
        )
    }
}
