package com.example.backend.usecase.impl

import com.example.backend.api.dto.request.RegisterRequest
import com.example.backend.api.dto.response.AuthResponse
import com.example.backend.api.mapper.UserMapper
import com.example.backend.infrastructure.security.jwt.JwtUtil
import com.example.backend.usecase.gateway.UserRepositoryPort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * 新規ユーザー登録に関するユースケースを実装するクラス。
 *
 * @property userRepository ユーザー情報へのアクセスを担うリポジトリポート。
 * @property passwordEncoder パスワードのハッシュ化を行うエンコーダー。
 * @property jwtUtil JWTトークンの生成を行うユーティリティ。
 */
@Service
class RegisterUseCase(
    private val userRepository: UserRepositoryPort,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {
    /**
     * 新規ユーザーを登録し、認証トークンを返却します。
     *
     * @param request 新規登録情報を含むリクエストDTO。
     * @return 生成された認証トークンを含むレスポンスDTO。
     * @throws IllegalArgumentException 既にメールアドレスが登録されている場合にスローされます。
     */
    fun execute(request: RegisterRequest): AuthResponse {
        // 既にメールアドレスが存在しているかチェック
        require(userRepository.findByEmail(request.email) == null) {
            "既に登録されているメールアドレスです。"
        }

        // パスワードをハッシュ化
        val encodedPassword = passwordEncoder.encode(request.password)

        // DTO → ドメインモデルへ変換
        val user = UserMapper.toDomain(request, encodedPassword)

        // ユーザーを保存
        val saved = userRepository.save(user)

        // JWTトークンを生成して返却（null安全性を確保）
        val token = saved.userId?.let { userId ->
            jwtUtil.generateToken(userId)
        } ?: throw IllegalStateException("User registration failed: User ID was not generated")
        
        return AuthResponse(token)
    }
}
