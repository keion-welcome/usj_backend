package com.example.backend.infrastructure.entity

import com.example.backend.shared.annotation.GeneratedUuid7
import jakarta.persistence.*
import jakarta.validation.constraints.*

/**
 * データベース上の「users」テーブルと対応するエンティティクラス。
 * JPA（Hibernate）により永続化されます。
 */
@Entity
@Table(name = "users")
class UserEntity(

    // 主キー（UUID7）- APIでも使用
    @Id
    @GeneratedUuid7
    @Column(nullable = false, length = 36)
    var id: String? = null,

    // メールアドレス（null不可、重複不可、形式検証）
    @Email
    @Size(max = 255)
    @Column(nullable = false, unique = true, length = 255)
    var email: String,

    // パスワード（ハッシュ化された文字列を保存）
    @Column(nullable = false, length = 255)
    var passwordHash: String,

    // プロフィールとの1対1リレーションシップ
    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var profile: ProfileEntity? = null

) : BaseEntity() {
    
    // JPA用のデフォルトコンストラクタ
    constructor() : this(
        id = null,  // @GeneratedUuid7で自動生成
        email = "",
        passwordHash = "",
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
        return "UserEntity(id=$id, email='$email')"
    }
}