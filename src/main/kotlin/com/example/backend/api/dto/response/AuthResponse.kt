package com.example.backend.api.dto.response
/**
 * 認証後に返されるレスポンスDTO。
 * JWTトークンをクライアントに返します。
 */
data class AuthResponse(
    val token: String,
    val isProfileCreated: Boolean
)