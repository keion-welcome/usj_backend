package com.example.backend.usecase.impl

import com.example.backend.api.dto.request.LoginRequest
import com.example.backend.api.dto.response.AuthResponse
import com.example.backend.usecase.gateway.AuthenticationPort
import com.example.backend.usecase.gateway.UserRepositoryPort
import org.springframework.stereotype.Service

/**
 * ログイン機能を実装するユースケースクラス
 *
 * @property authenticationPort 認証処理を担うポート
 * @property userRepository ユーザー情報へのアクセスを担うリポジトリポート
 */
@Service
class LoginUseCase(
    private val authenticationPort: AuthenticationPort,
    private val userRepository: UserRepositoryPort
) {
    /**
     * ログイン処理を実行する
     *
     * @param request ログイン情報（メールアドレスとパスワード）
     * @return 認証トークンを含むレスポンス
     */
    fun execute(request: LoginRequest): AuthResponse {
        // ユーザーを認証し、メールアドレスを取得
        val email = authenticationPort.authenticate(request.email, request.password)

        // メールアドレスを元にJWTトークンを生成
        val token = authenticationPort.generateToken(email)

        return AuthResponse(
            token = token
        )
    }
}
