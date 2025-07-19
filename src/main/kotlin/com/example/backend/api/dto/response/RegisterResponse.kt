package com.example.backend.api.dto.response

/**
 * 新規登録後に返されるレスポンスDTO。
 * JWTトークンをクライアントに返します。
 */
data class RegisterResponse(val token: String)
