package com.example.backend.api.controller

import com.example.backend.api.dto.request.LoginRequest
import com.example.backend.api.dto.request.RegisterRequest
import com.example.backend.api.dto.response.AuthResponse
import com.example.backend.usecase.impl.LoginUseCase
import com.example.backend.usecase.impl.LogoutUseCase
import com.example.backend.usecase.impl.RegisterUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
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
@Tag(name = "認証", description = "ユーザー認証関連のAPIエンドポイント")
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
    @Operation(
        summary = "ユーザー登録",
        description = "新しいユーザーアカウントを作成し、認証トークンを返します。"
    )
    fun register(
        @RequestBody request: RegisterRequest
    ): ResponseEntity<AuthResponse> {
        val authResponse = registerUseCase.execute(request)
        return ResponseEntity.ok(authResponse)
    }

    /**
     * POST /api/auth/login
     * ユーザーのログイン処理を行います。
     *
     * @param request ログイン情報（メールアドレスとパスワード）を含むリクエストボディ
     * @return ログイン成功時には、認証トークンを含むレスポンスを返します。
     */
    @PostMapping("/login")
    @Operation(
        summary = "ユーザーログイン",
        description = "メールアドレスとパスワードを使用してユーザーを認証し、JWTトークンを返します。"
    )
    fun login(
        @RequestBody request: LoginRequest
    ): ResponseEntity<AuthResponse> {
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
    @Operation(
        summary = "ユーザーログアウト",
        description = "JWTトークンを無効化し、ユーザーをログアウトさせます。"
    )
    fun logout(
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Void> {
        logoutUseCase.execute(token)
        return ResponseEntity.ok().build()
    }
}
