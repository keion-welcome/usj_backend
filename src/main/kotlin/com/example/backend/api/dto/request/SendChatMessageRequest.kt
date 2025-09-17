package com.example.backend.api.dto.request

/**
 * チャットメッセージ送信リクエスト
 *
 * @property chatRoomId チャットルームID
 * @property content メッセージ内容
 */
data class SendChatMessageRequest(
    val chatRoomId: Long,
    val content: String
)
