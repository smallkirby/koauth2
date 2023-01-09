package com.github.smallkirby.koauth2

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode

class AuthorizationCodeClient(
    private val config: AuthConfig,
    private val client: Client,
) {
    suspend fun refresh(token: AccessToken): AccessToken {
        val httpClient = HttpClient(CIO)
        if (token.refreshToken == null) throw AuthorizationCodeException("Refresh token not set")

        val res = httpClient.get("${config.tokenHost}/${config.refreshPath}") {
            parameter("grant_type", "refresh_token")
            parameter("refresh_token", token.refreshToken)
        }

        if (res.status == HttpStatusCode.OK) {
            return AccessToken.fromJsonString(res.bodyAsText())
        } else {
            throw AuthorizationCodeException("Invalid response from auth server: ${res.status}")
        }
    }
}

class AuthorizationCodeException(msg: String) : Exception(msg)
