package com.github.smallkirby.koauth2

import java.net.URL

data class AuthConfig(
    val tokenHost: URL,
    val tokenPath: String = "/oauth/token",
    val refreshPath: String = "/oauth/token",
    val revokePath: String = "/oauth/revoke",
    val authorizeHost: URL,
    val authorizePath: String = "/oauth/authorize",
)
