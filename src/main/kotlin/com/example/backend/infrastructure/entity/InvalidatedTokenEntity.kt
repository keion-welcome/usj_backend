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
data class InvalidatedTokenEntity(
    @Id
    @Column(length = 1000)  // JWTトークンは長いため
    val token: String,
    
    @Column(nullable = false)
    val expiryDate: Instant

) : BaseEntity()
