package com.example.backend.domain.model
/**
 * ドメイン層のユーザーモデル。
 * ビジネスルール上のユーザーの構造を表します。
 */
data class User(
    val id: String? = null,             // 主キー（UUID7）
    val username: String,               // ユーザー名
    val email: String,                  // メールアドレス（一意）
    val passwordHash: String            // ハッシュ化されたパスワード
)