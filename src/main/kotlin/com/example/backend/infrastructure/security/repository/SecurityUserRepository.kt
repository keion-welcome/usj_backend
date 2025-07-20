package com.example.backend.infrastructure.security.repository

/**
 * セキュリティ専用のユーザーリポジトリインターフェース
 * Spring Securityの要件に特化
 */
interface SecurityUserRepository {
    /**
     * メールアドレスでユーザーを検索する
     *
     * @param email メールアドレス
     * @return セキュリティ用ユーザー情報（存在しない場合はnull）
     */
    fun findByEmail(email: String): SecurityUser?
    
    /**
     * ユーザーID（UUID）でユーザーを検索する
     *
     * @param userId ユーザーID（UUID）
     * @return セキュリティ用ユーザー情報（存在しない場合はnull）
     */
    fun findByUserId(userId: String): SecurityUser?
}

/**
 * セキュリティ用のユーザー情報
 */
data class SecurityUser(
    val userId: String,  // UUID
    val email: String,
    val password: String
) 