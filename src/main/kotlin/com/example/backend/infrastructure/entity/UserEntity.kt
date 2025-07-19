package com.example.backend.infrastructure.entity

import jakarta.persistence.*
import jakarta.validation.constraints.*

/**
 * データベース上の「users」テーブルと対応するエンティティクラス。
 * JPA（Hibernate）により永続化されます。
 */
@Entity
@Table(name = "users")
class UserEntity(

    // 主キー（自動採番）
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    // ユーザー名（null不可、長さ制限）
    @Size(min = 1, max = 50)
    @Column(nullable = false, length = 50)
    var username: String,

    // メールアドレス（null不可、重複不可、形式検証）
    @Email
    @Size(max = 255)
    @Column(nullable = false, unique = true, length = 255)
    var email: String,

    // パスワード（ハッシュ化された文字列を保存）
    @Size(min = 8, max = 255)
    @Column(nullable = false, length = 255)
    var password: String,

    // プロフィールとの1対1リレーションシップ
    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var profile: ProfileEntity? = null

) : BaseEntity() {
    
    // JPA用のデフォルトコンストラクタ
    constructor() : this(
        id = null,
        username = "",
        email = "",
        password = "",
        profile = null
    )
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as UserEntity
        return id == other.id
    }
    
    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
    
    override fun toString(): String {
        return "UserEntity(id=$id, username='$username', email='$email')"
    }
}