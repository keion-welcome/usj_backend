package com.example.backend.usecase.impl

import com.example.backend.api.dto.request.LoginRequest
import com.example.backend.api.dto.response.AuthResponse
import com.example.backend.usecase.gateway.AuthenticationPort
import org.springframework.stereotype.Service

/**
 * ユーザーログインに関するユースケースを実装するクラス。
 *
 * @property authenticationPort 認証処理を行うポート
 */
@Service
class LoginUseCase(
    private val authenticationPort: AuthenticationPort
) {
    /**
     * ユーザーを認証し、認証トークンを返却します。
     *
     * @param request ログイン情報を含むリクエストDTO。
     * @return 生成された認証トークンを含むレスポンスDTO。
     */
    fun execute(request: LoginRequest): AuthResponse {
        // 認証処理を実行
        val email = authenticationPort.authenticate(request.email, request.password)
        
        // JWTトークンを生成
        val token = authenticationPort.generateToken(email)
        
        // 生成したトークンをレスポンスとして返却
        return AuthResponse(token)
    }
}
