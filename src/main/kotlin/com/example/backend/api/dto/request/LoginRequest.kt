package com.example.backend.api.dto.request

import jakarta.validation.constraints.*

/**
 * クライアントからのログインリクエストDTO。
 */
data class LoginRequest(
    @field:NotBlank
    @field:Email
    val email: String,
    
    @field:NotBlank
    val password: String
)