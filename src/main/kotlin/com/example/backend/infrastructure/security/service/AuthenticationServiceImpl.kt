package com.example.backend.infrastructure.security.service

import com.example.backend.infrastructure.security.jwt.JwtUtil
import com.example.backend.usecase.gateway.AuthenticationPort
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
    private val jwtUtil: JwtUtil
) : AuthenticationPort {

    /**
     * ユーザーを認証し、そのユーザーのメールアドレスを返す。
     *
     * @param email メールアドレス
     * @param password パスワード
     * @return 認証されたユーザーのメールアドレス
     */
    override fun authenticate(email: String, password: String): String {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(email, password)
        )
        SecurityContextHolder.getContext().authentication = authentication
        return email // 認証されたユーザーのメールアドレスを返す
    }

    /**
     * メールアドレスを元にJWTトークンを生成する
     *
     * @param email メールアドレス
     * @return 生成されたJWTトークン
     */
    override fun generateToken(email: String): String {
        return jwtUtil.generateToken(email)
    }
} 