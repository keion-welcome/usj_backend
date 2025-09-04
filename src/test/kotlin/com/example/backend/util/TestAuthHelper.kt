package com.example.backend.util

import com.example.backend.domain.model.User
import com.example.backend.infrastructure.security.jwt.JwtUtil
import org.springframework.boot.test.context.TestComponent
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.test.context.support.WithSecurityContextFactory
import java.util.*

/**
 * テスト用認証ヘルパークラス
 * 
 * 認証が必要なテストで使用する共通機能を提供します。
 * モックユーザーの作成、JWTトークンの生成、認証コンテキストの設定を行います。
 */
@TestComponent
class TestAuthHelper {

    /**
     * モックユーザーでセキュリティコンテキストを設定
     * 
     * @param userId ユーザーID
     * @return 設定されたセキュリティコンテキスト
     */
    fun withMockUser(userId: String): SecurityContext {
        val authentication = createMockAuthentication(userId)
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authentication
        SecurityContextHolder.setContext(context)
        return context
    }

    /**
     * テスト用JWTトークンを生成
     * 
     * @param userId ユーザーID
     * @return 生成されたJWTトークン
     */
    fun generateTestJWT(userId: String): String {
        // テスト用の簡易JWT生成（実際のJwtUtilは使用せず、テスト用の固定値）
        return "test.jwt.token.$userId"
    }

    /**
     * ユーザーオブジェクトからモック認証を作成
     * 
     * @param user ユーザーオブジェクト
     * @return 認証オブジェクト
     */
    fun mockAuthentication(user: User): Authentication {
        return createMockAuthentication(user.id!!)
    }

    /**
     * テスト用ユーザーを作成
     * 
     * @param userId ユーザーID（省略時はランダム生成）
     * @param email メールアドレス（省略時はデフォルト値）
     * @return テスト用ユーザー
     */
    fun createTestUser(
        userId: String = UUID.randomUUID().toString(),
        email: String = "test@example.com"
    ): User {
        return User(
            id = userId,
            email = email,
            passwordHash = "hashedPassword"
        )
    }

    /**
     * 認証済み状態をクリア
     */
    fun clearAuthentication() {
        SecurityContextHolder.clearContext()
    }

    private fun createMockAuthentication(userId: String): Authentication {
        val userDetails = org.springframework.security.core.userdetails.User(
            userId,
            "password",
            emptyList()
        )
        return UsernamePasswordAuthenticationToken(userDetails, null, emptyList())
    }
} 