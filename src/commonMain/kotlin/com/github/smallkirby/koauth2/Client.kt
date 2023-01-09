package com.github.smallkirby.koauth2

data class Client(
    private val id: String,
    private val secret: String,
    private val idPropertyName: String = "client_id",
    private val secretPropertyName: String = "client_secret",
)
