package com.example.backend.domain.model

/**
 * チャットルーム ドメインモデル
 *
 * @property id チャットルームID
 * @property recruitmentId 紐づく募集ID
 * @property participants 参加ユーザーIDのリスト
 */
data class ChatRoom(
    val id: Long,
    val recruitmentId: Long,
    val participants: List<Long>
)
