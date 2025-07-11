package com.example.backend.infrastructure.entity


import jakarta.persistence.*

/**
 * データベース上の「users」テーブルと対応するエンティティクラス。
 * JPA（Hibernate）により永続化されます。
 */
@Entity
@Table(name = "users")
data class UserEntity(

    // 主キー（自動採番）
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    // ユーザー名（null不可）
    @Column(nullable = false)
    val username: String,

    // メールアドレス（null不可、重複不可）
    @Column(nullable = false, unique = true)
    val email: String,

    // パスワード（ハッシュ化された文字列を保存）
    @Column(nullable = false)
    val password: String
)