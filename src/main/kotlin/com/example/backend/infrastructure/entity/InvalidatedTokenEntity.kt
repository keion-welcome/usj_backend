package com.example.backend.infrastructure.entity

import jakarta.persistence.*
import java.time.Instant

/**
 * 無効化されたJWTトークンを表すJPAエンティティ。
 *
 * @property token 無効化されたトークンの文字列（主キー）。
 * @property expiryDate トークンの有効期限。
 */
@Entity
@Table(
    name = "invalidated_tokens",
    indexes = [
        Index(name = "idx_invalidated_tokens_expiry", columnList = "expiryDate")
    ]
)
class InvalidatedTokenEntity(
    @Id
    @Column(length = 1000)  // JWTトークンは長いため
    var token: String,
    
    @Column(nullable = false)
    var expiryDate: Instant

) : BaseEntity() {
    
    // JPA用のデフォルトコンストラクタ
    constructor() : this("", Instant.now())
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as InvalidatedTokenEntity
        return token == other.token
    }
    
    override fun hashCode(): Int {
        return token.hashCode()
    }
    
    override fun toString(): String {
        return "InvalidatedTokenEntity(token='$token', expiryDate=$expiryDate)"
    }
}
