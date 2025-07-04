//永続化操作のインターフェース
package com.example.backend.domain.repository

import com.example.backend.domain.model.User
/**
 * ドメイン層で定義されるリポジトリのインターフェース。
 * ユーザーの永続化処理の契約を定義します。
 */
interface UserRepository {
    fun save(user: User): User                // ユーザーを保存（新規登録）
    fun findByEmail(email: String): User?     // メールアドレスでユーザーを検索
}