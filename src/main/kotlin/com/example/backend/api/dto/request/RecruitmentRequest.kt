package com.example.backend.api.dto.request

/**
 * 募集参加リクエスト
 *
 * @property recruitmentId 募集ID
 */
data class JoinRecruitmentRequest(
    val recruitmentId: Long
)

/**
 * 募集退出リクエスト
 *
 * @property recruitmentId 募集ID
 */
data class LeaveRecruitmentRequest(
    val recruitmentId: Long
)

/**
 * 募集更新リクエスト
 *
 * @property id 募集ID
 * @property title 募集のタイトル
 * @property description 募集の詳細な説明
 */
data class UpdateRecruitmentRequest(
    val id: Long,
    val title: String,
    val description: String
)