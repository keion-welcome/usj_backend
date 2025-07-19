package com.example.backend.api.dto.request

import java.time.LocalDate

/**
 * プロフィール更新リクエストのDTO
 *
 * @property nickname ニックネーム
 * @property gender 性別
 * @property birthdate 生年月日
 * @property area 居住エリア
 * @property occupation 学年・職業
 * @property hasAnnualPass 年パスの有無
 */
data class UpdateProfileRequest(
    val nickname: String,
    val gender: String,
    val birthdate: LocalDate,
    val area: String,
    val occupation: String,
    val hasAnnualPass: Boolean
)
