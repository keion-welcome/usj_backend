package com.example.backend.api.controller

import com.example.backend.api.dto.request.LoginRequest
import com.example.backend.api.dto.request.RegisterRequest
import com.example.backend.api.dto.response.AuthResponse
import com.example.backend.api.dto.response.RegisterResponse
import com.example.backend.usecase.usecase.LoginUseCase
import com.example.backend.usecase.usecase.LogoutUseCase
import com.example.backend.usecase.usecase.RegisterUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 認証関連のAPIエンドポイントを提供するコントローラーです。
 * 新規登録とログインの機能を提供します。
 *
 * @property registerUseCase 新規登録処理のユースケース
 * @property loginUseCase ログイン処理のユースケース
 * @property logoutUseCase ログアウト処理のユースケース
 */
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase
) {

    /**
     * POST /api/auth/register
     * 新しいユーザーを登録します。
     *
     * @param request 登録するユーザーの情報を含むリクエストボディ
     * @return 登録成功時には、認証トークンを含むレスポンスを返します。
     */
    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<RegisterResponse> {
        val response = registerUseCase.execute(request)
        return ResponseEntity.ok(response)
    }

    /**
     * POST /api/auth/login
     * ユーザーのログイン処理を行います。
     *
     * @param request ログイン情報（メールアドレスとパスワード）を含むリクエストボディ
     * @return ログイン成功時には、認証トークンを含むレスポンスを返します。
     */
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        val authResponse = loginUseCase.execute(request)
        return ResponseEntity.ok(authResponse)
    }

    /**
     * POST /api/auth/logout
     * ユーザーのログアウト処理を行います。
     *
     * @param token 認証トークン
     * @return ログアウト成功時には、ステータスコード200を返します。
     */
    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization") token: String): ResponseEntity<Void> {
        logoutUseCase.execute(token)
        return ResponseEntity.ok().build()
    }
}
