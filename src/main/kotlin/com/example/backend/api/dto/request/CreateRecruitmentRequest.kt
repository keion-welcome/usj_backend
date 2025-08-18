package com.example.backend.api.dto.request

import java.time.LocalDateTime

/**
 * 募集作成リクエスト
 *
 * @property title 募集のタイトル
 * @property description 募集の詳細な説明
 * @property attractionId アトラクションID
 * @property maxParticipants 最大参加者数
 * @property expiresAt 募集締切日時
 */
data class CreateRecruitmentRequest(
    val title: String,
    val description: String,
    val attractionId: Long? = null,
    val maxParticipants: Int = 4,
    val expiresAt: LocalDateTime? = null
)
