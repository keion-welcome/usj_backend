package com.example.backend.domain.model
/**
 * ドメイン層のユーザーモデル。
 * ビジネスルール上のユーザーの構造を表します。
 */
data class User(
    val id: Long? = null,           // 内部用ID（DBにより自動採番）
    val userId: String? = null,     // 外部用ID（UUID）
    val username: String,           // ユーザー名
    val email: String,              // メールアドレス（一意）
    val password: String            // ハッシュ化されたパスワード
)