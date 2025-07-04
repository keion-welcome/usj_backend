package com.example.backend.api.dto.request

/**
 * クライアントからの新規登録リクエストDTO。
 * JSONをこの形で受け取ります。
 */
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)