package com.example.backend.infrastructure.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

/**
 * 無効化されたJWTトークンを表すJPAエンティティ。
 *
 * @property token 無効化されたトークンの文字列（主キー）。
 * @property expiryDate トークンの有効期限。
 */
@Entity
@Table(name = "invalidated_tokens")
data class InvalidatedTokenEntity(
    @Id
    val token: String,
    val expiryDate: Instant
)
