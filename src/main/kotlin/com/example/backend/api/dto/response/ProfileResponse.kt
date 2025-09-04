package com.example.backend.api.dto.response

import java.time.LocalDate

/**
 * プロフィールレスポンスのDTO
 *
 * @property id プロフィールID
 * @property userId ユーザーID
 * @property nickname ニックネーム
 * @property gender 性別
 * @property birthdate 生年月日
 * @property area 居住エリア
 * @property occupation 学年・職業
 * @property hasAnnualPass 年パスの有無
 */
data class ProfileResponse(
    val id: Long?,
    val userId: String?,
    val nickname: String,
    val gender: String,
    val birthdate: LocalDate,
    val area: String,
    val occupation: String,
    val hasAnnualPass: Boolean
)
