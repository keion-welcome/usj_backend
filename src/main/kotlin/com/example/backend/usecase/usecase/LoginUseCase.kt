package com.example.backend.usecase.usecase

import com.example.backend.api.dto.request.LoginRequest
import com.example.backend.api.dto.response.AuthResponse
import com.example.backend.usecase.gateway.ProfileRepositoryPort
import com.example.backend.usecase.gateway.UserRepositoryPort
import com.example.backend.infrastructure.security.jwt.JwtUtil
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

/**
 * ユーザーログインに関するユースケースを実装するクラス。
 *
 * @property authenticationManager Spring Securityの認証処理を行うマネージャー。
 * @property jwtUtil JWTトークンの生成を行うユーティリティ。
 * @property userRepositoryPort ユーザーリポジトリのポート。
 * @property profileRepositoryPort プロフィールリポジトリのポート。
 */
@Service
class LoginUseCase(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil,
    private val userRepositoryPort: UserRepositoryPort,
    private val profileRepositoryPort: ProfileRepositoryPort
) {
    /**
     * ユーザーを認証し、認証トークンとプロフィールの有無を返却します。
     *
     * @param request ログイン情報を含むリクエストDTO。
     * @return 生成された認証トークンとプロフィールの有無を含むレスポンスDTO。
     */
    fun execute(request: LoginRequest): AuthResponse {
        // Spring SecurityのAuthenticationManagerを使用して認証を実行します。
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )

        // 認証が成功した場合、SecurityContextHolderに認証情報を設定します。
        SecurityContextHolder.getContext().authentication = authentication

        // 認証されたユーザーの情報を取得します。
        val user = userRepositoryPort.findByEmail(request.email)
            ?: throw IllegalStateException("User not found with email: ${request.email}")

        // プロフィールが作成されているかを確認します。
        val profile = profileRepositoryPort.findByUserId(user.id!!)
        val isProfileCreated = profile != null

        // JWTトークンを生成します。
        val token = jwtUtil.generateToken(request.email)

        // トークンとプロフィールの有無をレスポンスとして返却します。
        return AuthResponse(token, isProfileCreated)
    }
}
