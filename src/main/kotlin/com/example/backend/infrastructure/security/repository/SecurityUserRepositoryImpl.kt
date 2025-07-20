package com.example.backend.infrastructure.security.repository

import com.example.backend.usecase.gateway.UserRepositoryPort
import org.springframework.stereotype.Repository

/**
 * SecurityUserRepositoryの実装クラス
 * ユースケース層のUserRepositoryPortをアダプト
 */
@Repository
class SecurityUserRepositoryImpl(
    private val userRepositoryPort: UserRepositoryPort
) : SecurityUserRepository {

    /**
     * メールアドレスでユーザーを検索する
     *
     * @param email メールアドレス
     * @return セキュリティ用ユーザー情報（存在しない場合はnull）
     */
    override fun findByEmail(email: String): SecurityUser? {
        return userRepositoryPort.findByEmail(email)?.let { user ->
            SecurityUser(
                userId = user.userId ?: "",
                email = user.email,
                password = user.password
            )
        }
    }
    
    /**
     * ユーザーID（UUID）でユーザーを検索する
     *
     * @param userId ユーザーID（UUID）
     * @return セキュリティ用ユーザー情報（存在しない場合はnull）
     */
    override fun findByUserId(userId: String): SecurityUser? {
        return userRepositoryPort.findByUserId(userId)?.let { user ->
            SecurityUser(
                userId = user.userId ?: "",
                email = user.email,
                password = user.password
            )
        }
    }
} 