package com.example.backend.usecase

import com.example.backend.api.dto.request.LoginRequest
import com.example.backend.api.dto.request.RegisterRequest
import com.example.backend.api.dto.response.AuthResponse
import com.example.backend.api.mapper.UserMapper
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import com.example.backend.usecase.port.UserRepositoryPort
import com.example.backend.infrastructure.security.JwtUtil

/**
 * 認証（登録・ログイン）に関するユースケースを実装。
 * ユーザー情報の保存・トークン発行などを行う。
 */
@Service
class AuthUseCase(
    private val userRepository: UserRepositoryPort,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {

    /**
     * 新規ユーザー登録処理
     */
    fun register(request: RegisterRequest): AuthResponse {
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

        // JWTトークンを生成して返却
        val token = jwtUtil.generateToken(saved.email)
        return AuthResponse(token)
    }

    /**
     * ログイン処理
     */
    fun login(request: LoginRequest): AuthResponse {
        // メールアドレスからユーザーを検索
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("ユーザーが見つかりません")

        // パスワード検証
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("パスワードが正しくありません")
        }

        // トークン生成
        val token = jwtUtil.generateToken(user.email)
        return AuthResponse(token)
    }
}
