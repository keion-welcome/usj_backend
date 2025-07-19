package com.example.backend.infrastructure.entity

import jakarta.persistence.*
import jakarta.validation.constraints.*

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

    // ユーザー名（null不可、長さ制限）
    @Size(min = 1, max = 50)
    @Column(nullable = false, length = 50)
    val username: String,

    // メールアドレス（null不可、重複不可、形式検証）
    @Email
    @Size(max = 255)
    @Column(nullable = false, unique = true, length = 255)
    val email: String,

    // パスワード（ハッシュ化された文字列を保存）
    @Size(min = 8, max = 255)
    @Column(nullable = false, length = 255)
    val password: String,

    // プロフィールとの1対1リレーションシップ
    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val profile: ProfileEntity? = null

) : BaseEntity()