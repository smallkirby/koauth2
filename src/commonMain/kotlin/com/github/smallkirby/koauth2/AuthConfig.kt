package com.github.smallkirby.koauth2

import java.net.URL

data class AuthConfig(
    private val tokenHost: URL,
    private val tokenPath: String = "/oauth/token",
    private val refreshPath: String = "/oauth/token",
    private val revokePath: String = "/oauth/revoke",
    private val authorizeHost: URL,
    private val authorizePath: String = "/oauth/authorize",
)
