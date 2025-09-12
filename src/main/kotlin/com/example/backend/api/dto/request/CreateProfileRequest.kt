package com.example.backend.api.dto.request

import jakarta.validation.constraints.*
import java.time.LocalDate

/**
 * プロフィール作成リクエストのDTO
 *
 * @property userId ユーザーID
 * @property nickname ニックネーム
 * @property gender 性別
 * @property birthdate 生年月日
 * @property area 居住エリア
 * @property occupation 学年・職業
 * @property hasAnnualPass 年パスの有無
 */
data class CreateProfileRequest(
    val userId: String?,

    @field:NotBlank
    @field:Size(min = 1, max = 50)
    val nickname: String,

    @field:NotBlank
    val gender: String,

    @field:NotNull
    @field:Past
    val birthdate: LocalDate,

    @field:NotBlank
    @field:Size(max = 100)
    val area: String,

    @field:NotBlank
    @field:Size(max = 100)
    val occupation: String,

    @field:NotNull
    val hasAnnualPass: Boolean
)
