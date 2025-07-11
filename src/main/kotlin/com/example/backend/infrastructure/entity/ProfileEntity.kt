package com.example.backend.infrastructure.entity

import jakarta.persistence.*
import java.time.LocalDate

/**
 * プロフィール情報を表すJPAエンティティ
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
@Entity
@Table(name = "profiles")
data class ProfileEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val nickname: String,

    @Column(nullable = false)
    val gender: String,

    @Column(nullable = false)
    val birthdate: LocalDate,

    @Column(nullable = false)
    val area: String,

    @Column(nullable = false)
    val occupation: String,

    @Column(nullable = false)
    val hasAnnualPass: Boolean
)
