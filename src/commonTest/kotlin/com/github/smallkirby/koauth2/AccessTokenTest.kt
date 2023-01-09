package com.github.smallkirby.koauth2

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
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

    private fun ApplicationTestBuilder.mockAuthServer() {
        externalServices {
            hosts("https://google.com") {
                routing {
                    get("/") {
                        call.respond("Mocked Google!")
                    }
                }
            }
        }
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

        // Check expiration
        assertEquals(
            false,
            accessToken.expired(),
        )

        assertEquals(
            false,
            accessToken.expiresIn(30),
        )
        assertEquals(
            true,
            accessToken.expiresIn(3700),
        )
    }

    @Test
    fun testKtorMockHealth() = testApplication {
        mockAuthServer()

        val response = client.get("https://google.com")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Mocked Google!", response.bodyAsText())
    }
}
