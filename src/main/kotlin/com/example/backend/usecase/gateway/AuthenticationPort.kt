package com.example.backend.usecase.gateway

/**
 * 認証に関するポートインターフェース
 */
interface AuthenticationPort {
    /**
     * ユーザーを認証する
     *
     * @param email メールアドレス
     * @param password パスワード
     * @return 認証されたユーザーのuserId（UUID）
     */
    fun authenticate(email: String, password: String): String
    
    /**
     * JWTトークンを生成する
     *
     * @param userId ユーザーID（UUID）
     * @return 生成されたJWTトークン
     */
    fun generateToken(userId: String): String
} 