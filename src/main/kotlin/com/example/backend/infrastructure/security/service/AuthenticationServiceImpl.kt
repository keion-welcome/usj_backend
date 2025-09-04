package com.example.backend.infrastructure.security.service

import com.example.backend.infrastructure.security.jwt.JwtUtil
import com.example.backend.usecase.gateway.AuthenticationPort
import com.example.backend.usecase.gateway.UserRepositoryPort
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

/**
 * 認証サービスの実装クラス
 */
@Service
class AuthenticationServiceImpl(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepositoryPort
) : AuthenticationPort {

    /**
     * ユーザーを認証する
     *
     * @param email メールアドレス
     * @param password パスワード
     * @return 認証されたユーザーのuserId（UUID）
     */
    override fun authenticate(email: String, password: String): String {
        // まずemailでユーザーを検索してuserIdを取得
        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("Invalid credentials")
        
        // userIdを使って認証
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(user.id, password)
        )
        
        SecurityContextHolder.getContext().authentication = authentication
        
        return user.id!!
    }

    /**
     * JWTトークンを生成する
     *
     * @param userId ユーザーID（UUID）
     * @return 生成されたJWTトークン
     */
    override fun generateToken(userId: String): String {
        return jwtUtil.generateToken(userId)
    }
} 