package com.example.backend.usecase.gateway

import com.example.backend.domain.model.User

/**
 * ユーザーリポジトリのポートインターフェース
 */
interface UserRepositoryPort {
    /**
     * ユーザーを保存する
     *
     * @param user 保存するユーザー
     * @return 保存されたユーザー
     */
    fun save(user: User): User

    /**
     * メールアドレスでユーザーを検索する
     *
     * @param email メールアドレス
     * @return ユーザー（存在しない場合はnull）
     */
    fun findByEmail(email: String): User?

    /**
     * IDでユーザーを検索する
     *
     * @param id ユーザーID
     * @return ユーザー（存在しない場合はnull）
     */
    fun findById(id: Long): User?
    
    /**
     * ユーザーID（UUID）でユーザーを検索する
     *
     * @param userId ユーザーID（UUID）
     * @return ユーザー（存在しない場合はnull）
     */
    fun findByUserId(userId: String): User?
}