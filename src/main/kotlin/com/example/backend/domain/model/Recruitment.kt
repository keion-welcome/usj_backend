package com.example.backend.domain.model

import java.time.LocalDateTime

/**
 * 募集 ドメインモデル
 *
 * @property id 募集の一意なID
 * @property title 募集のタイトル
 * @property description 募集の詳細な説明
 * @property userId 募集を作成したユーザーのID
 * @property maxParticipants 最大参加者数
 * @property status 募集ステータス
 * @property expiresAt 募集締切日時
 * @property attractionName アトラクション名
 * @property attraction アトラクション情報
 * @property participants 参加者リスト
 * @property createdAt 募集の作成日時
 * @property updatedAt 募集の最終更新日時
 */
data class Recruitment(
    val id: Long? = null,
    val title: String,
    val description: String,
    val userId: Long,
    val maxParticipants: Int = 4,
    val status: RecruitmentStatus = RecruitmentStatus.ACTIVE,
    val expiresAt: LocalDateTime? = null,
    val attractionName: String? = null,
    val attraction: Attraction? = null,
    val participants: List<RecruitmentParticipant> = emptyList(),
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    /**
     * 募集が満員かどうかを判定
     */
    fun isFull(): Boolean = participants.size >= maxParticipants

    /**
     * ユーザーが参加しているかどうかを判定
     */
    fun isParticipating(userId: Long): Boolean = participants.any { it.userId == userId }

    /**
     * 募集がアクティブかどうかを判定
     */
    fun isActive(): Boolean = status == RecruitmentStatus.ACTIVE && !isFull()
}

/**
 * 募集ステータス
 */
enum class RecruitmentStatus {
    ACTIVE,
    COMPLETED,
    CANCELLED
}

/**
 * 募集参加者
 *
 * @property userId 参加者のユーザーID
 * @property joinedAt 参加日時
 */
data class RecruitmentParticipant(
    val userId: Long,
    val joinedAt: LocalDateTime
)
