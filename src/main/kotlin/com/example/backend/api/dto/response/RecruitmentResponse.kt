package com.example.backend.api.dto.response

import com.example.backend.domain.model.Recruitment
import com.example.backend.domain.model.RecruitmentParticipant
import com.example.backend.domain.model.RecruitmentStatus
import java.time.LocalDateTime

/**
 * 募集レスポンス
 *
 * @property id 募集の一意なID
 * @property title 募集のタイトル
 * @property description 募集の詳細な説明
 * @property userId 募集を作成したユーザーのID
 * @property maxParticipants 最大参加者数
 * @property currentParticipants 現在の参加者数
 * @property status 募集ステータス
 * @property expiresAt 募集締切日時
 * @property attraction アトラクション情報
 * @property participants 参加者リスト
 * @property isFull 満員かどうか
 * @property createdAt 募集の作成日時
 * @property updatedAt 募集の最終更新日時
 */
data class RecruitmentResponse(
    val id: Long,
    val title: String,
    val description: String,
    val userId: Long,
    val maxParticipants: Int,
    val currentParticipants: Int,
    val status: RecruitmentStatus,
    val expiresAt: LocalDateTime?,
    val attraction: AttractionResponse?,
    val participants: List<ParticipantResponse>,
    val isFull: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(recruitment: Recruitment): RecruitmentResponse {
            return RecruitmentResponse(
                id = recruitment.id!!,
                title = recruitment.title,
                description = recruitment.description,
                userId = recruitment.userId,
                maxParticipants = recruitment.maxParticipants,
                currentParticipants = recruitment.participants.size,
                status = recruitment.status,
                expiresAt = recruitment.expiresAt,
                attraction = recruitment.attraction?.let { AttractionResponse.from(it) },
                participants = recruitment.participants.map { ParticipantResponse.from(it) },
                isFull = recruitment.isFull(),
                createdAt = recruitment.createdAt,
                updatedAt = recruitment.updatedAt
            )
        }
    }
}

/**
 * 参加者レスポンス
 *
 * @property userId 参加者のユーザーID
 * @property joinedAt 参加日時
 */
data class ParticipantResponse(
    val userId: Long,
    val joinedAt: LocalDateTime
) {
    companion object {
        fun from(participant: RecruitmentParticipant): ParticipantResponse {
            return ParticipantResponse(
                userId = participant.userId,
                joinedAt = participant.joinedAt
            )
        }
    }
}
