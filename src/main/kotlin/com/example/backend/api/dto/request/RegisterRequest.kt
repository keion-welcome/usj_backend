package com.example.backend.api.dto.request

import jakarta.validation.constraints.*

/**
 * クライアントからの新規登録リクエストDTO。
 * JSONをこの形で受け取ります。
 */
data class RegisterRequest(
    @field:NotBlank
    @field:Email
    @field:Size(max = 255)
    val email: String,

    @field:NotBlank
    @field:Size(min = 8, max = 100)
    val password: String
)