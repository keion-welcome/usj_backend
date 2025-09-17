package com.example.backend.domain.model

import java.time.LocalDateTime

/**
 * チャットメッセージ ドメインモデル
 *
 * @property id メッセージID
 * @property chatRoomId チャットルームID
 * @property userId 送信ユーザーID
 * @property userNickname 送信ユーザーのニックネーム
 * @property userIconUrl 送信ユーザーのアイコンURL
 * @property content メッセージ内容
 * @property timestamp 送信日時
 */
data class ChatMessage(
    val id: Long,
    val chatRoomId: Long,
    val userId: Long,
    val userNickname: String,
    val userIconUrl: String?,
    val content: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
