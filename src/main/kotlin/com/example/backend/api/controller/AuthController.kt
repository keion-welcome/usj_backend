package com.example.backend.api.controller

import com.example.backend.api.dto.request.LoginRequest
import com.example.backend.api.dto.request.RegisterRequest
import com.example.backend.api.dto.response.AuthResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.example.backend.usecase.usecase.AuthUseCase

/**
 * 認証用のAPIエンドポイントを提供するコントローラー
 */
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authUseCase: AuthUseCase
) {

    /**
     * POST /api/auth/register
     * ユーザー登録処理
     */
    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authUseCase.register(request))
    }

    /**
     * POST /api/auth/login
     * ログイン処理
     */
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authUseCase.login(request))
    }
}
