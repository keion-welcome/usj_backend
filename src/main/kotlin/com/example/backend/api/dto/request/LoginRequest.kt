package com.example.backend.api.dto.request
/**
 * クライアントからのログインリクエストDTO。
 */
data class LoginRequest(
    val email: String,
    val password: String
)