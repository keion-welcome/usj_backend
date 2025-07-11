package com.example.backend.domain.model

import java.time.Instant

/**
 * 無効化されたJWTトークンを表すドメインモデル。
 *
 * @property token 無効化されたトークンの文字列。
 * @property expiryDate トークンの有効期限。この時間を過ぎたトークンはデータベースから削除してもよい。
 */
data class InvalidatedToken(
    val token: String,
    val expiryDate: Instant
)
