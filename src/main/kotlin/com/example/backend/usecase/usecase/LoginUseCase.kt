package com.example.backend.usecase.usecase

import com.example.backend.api.dto.request.LoginRequest
import com.example.backend.api.dto.response.AuthResponse
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
 */
@Service
class LoginUseCase(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil
) {
    /**
     * ユーザーを認証し、認証トークンを返却します。
     *
     * @param request ログイン情報を含むリクエストDTO。
     * @return 生成された認証トークンを含むレスポンスDTO。
     */
    fun execute(request: LoginRequest): AuthResponse {
        // Spring SecurityのAuthenticationManagerを使用して認証を実行します。
        // UsernamePasswordAuthenticationTokenにユーザーが入力したメールアドレスとパスワードをセットして認証処理を呼び出します。
        // 認証が成功すると、認証されたユーザーの情報を持つAuthenticationオブジェクトが返されます。
        // 認証に失敗した場合は、AuthenticationExceptionがスローされます。
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )

        // 認証が成功した場合、SecurityContextHolderに認証情報を設定します。
        // これにより、後続のリクエストで認証状態を維持できます。
        SecurityContextHolder.getContext().authentication = authentication

        // 認証されたユーザーのメールアドレスを元にJWTトークンを生成します。
        val token = jwtUtil.generateToken(request.email)

        // 生成したトークンをレスポンスとして返却します。
        return AuthResponse(token)
    }
}
