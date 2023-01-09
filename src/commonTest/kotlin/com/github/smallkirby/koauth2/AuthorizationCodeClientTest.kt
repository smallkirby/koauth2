package com.github.smallkirby.koauth2

import io.ktor.client.request.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test

class AuthorizationCodeClientTest {
    private fun ApplicationTestBuilder.mockAuthServer() {
        externalServices {
            hosts("https://example.com") {
                routing {
                    get("/oauth/refresh") {
                        call.respond("Mocked Refresh!")
                    }
                }
            }
        }
    }

    @Test
    fun testRefreshToken() = testApplication {
        mockAuthServer()

        val res = client.get("https://example.com/oauth/refresh")
        TODO()
    }
}
