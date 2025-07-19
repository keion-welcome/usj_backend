package com.example.backend.infrastructure.entity

import com.example.backend.domain.model.Gender
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import java.time.LocalDate

/**
 * プロフィール情報を表すJPAエンティティ
 *
 * @property id プロフィールの一意なID
 * @property user 関連するユーザーエンティティ（1対1関係）
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

    // ユーザーとの1対1リレーションシップ
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    val user: UserEntity,

    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    val nickname: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val gender: Gender,

    @Column(nullable = false)
    val birthdate: LocalDate,

    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    val area: String,

    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    val occupation: String,

    @Column(nullable = false)
    val hasAnnualPass: Boolean

) : BaseEntity()
