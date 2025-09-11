package com.example.backend.api.dto.response

import com.example.backend.domain.model.RecruitmentStatus
import java.time.LocalDateTime

/**
 * WebSocketメッセージタイプ
 */
enum class WebSocketMessageType {
    RECRUITMENT_UPDATED,
    PARTICIPANT_JOINED,
    PARTICIPANT_LEFT,
    RECRUITMENT_COMPLETED,
    RECRUITMENT_CANCELLED,
    ERROR
}

/**
 * WebSocketレスポンス
 *
 * @property type メッセージタイプ
 * @property recruitmentId 募集ID
 * @property data メッセージデータ
 * @property timestamp タイムスタンプ
 */
data class WebSocketResponse(
    val type: WebSocketMessageType,
    val recruitmentId: Long,
    val data: Any? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

/**
 * 参加者が参加した時のWebSocketメッセージ
 *
 * @property userId 参加したユーザーID
 * @property currentParticipants 現在の参加者数
 * @property maxParticipants 最大参加者数
 * @property isFull 満員かどうか
 */
data class ParticipantJoinedMessage(
    val userId: Long,
    val currentParticipants: Int,
    val maxParticipants: Int,
    val isFull: Boolean
)

/**
 * 参加者が退出した時のWebSocketメッセージ
 *
 * @property userId 退出したユーザーID
 * @property currentParticipants 現在の参加者数
 * @property maxParticipants 最大参加者数
 * @property isFull 満員かどうか
 */
data class ParticipantLeftMessage(
    val userId: Long,
    val currentParticipants: Int,
    val maxParticipants: Int,
    val isFull: Boolean
)

/**
 * 募集が更新された時のWebSocketメッセージ
 *
 * @property recruitment 更新された募集情報
 */
data class RecruitmentUpdatedMessage(
    val recruitment: RecruitmentResponse
)

/**
 * 募集が完了/キャンセルされた時のWebSocketメッセージ
 *
 * @property status 新しいステータス
 * @property reason 理由（オプション）
 */
data class RecruitmentStatusChangedMessage(
    val status: RecruitmentStatus,
    val reason: String? = null
)

/**
 * エラーメッセージ
 *
 * @property message エラーメッセージ
 * @property code エラーコード
 */
data class ErrorMessage(
    val message: String,
    val code: String? = null
)