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
     * @return 認証されたユーザーのメールアドレス
     */
    fun authenticate(email: String, password: String): String
    
    /**
     * JWTトークンを生成する
     *
     * @param email メールアドレス
     * @return 生成されたJWTトークン
     */
    fun generateToken(email: String): String
} 