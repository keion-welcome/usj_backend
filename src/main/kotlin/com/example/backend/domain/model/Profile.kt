package com.example.backend.domain.model

import java.time.LocalDate

/**
 * プロフィール情報を表すドメインモデル
 *
 * @property id プロフィールの一意なID
 * @property userId ユーザーID
 * @property nickname ニックネーム
 * @property gender 性別
 * @property birthdate 生年月日
 * @property area 居住エリア
 * @property occupation 学年・職業
 * @property hasAnnualPass 年パスの有無
 */
data class Profile(
    val id: Long? = null,
    val userId: Long,
    val nickname: String,
    val gender: String,
    val birthdate: LocalDate,
    val area: String,
    val occupation: String,
    val hasAnnualPass: Boolean
)
