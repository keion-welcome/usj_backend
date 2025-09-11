package com.example.backend.usecase.gateway

/**
 * 認証に関する処理のインターフェース
 */
interface AuthenticationPort {
    /**
     * ユーザーを認証し、そのユーザーのメールアドレスを返す。
     *
     * @param email メールアドレス
     * @param password パスワード
     * @return 認証されたユーザーのメールアドレス
     */
    fun authenticate(email: String, password: String): String

    /**
     * メールアドレスを元にJWTトークンを生成する
     *
     * @param email メールアドレス
     * @return 生成されたJWTトークン
     */
    fun generateToken(email: String): String
} 