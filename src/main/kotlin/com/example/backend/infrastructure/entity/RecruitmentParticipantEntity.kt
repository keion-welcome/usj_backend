package com.example.backend.infrastructure.entity

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 募集参加者エンティティ
 *
 * @property id 参加記録の一意なID
 * @property recruitment 参加している募集
 * @property user 参加しているユーザー
 * @property joinedAt 参加日時
 */
@Entity
@Table(name = "recruitment_participants")
@IdClass(RecruitmentParticipantId::class)
data class RecruitmentParticipantEntity(
    @Id
    @Column(name = "recruitment_id")
    val recruitmentId: Long,

    @Id
    @Column(name = "user_id")
    val userId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id", insertable = false, updatable = false)
    val recruitment: RecruitmentEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    val user: UserEntity?,

    @Column(name = "joined_at", nullable = false)
    val joinedAt: LocalDateTime = LocalDateTime.now()
)

/**
 * 複合主キー用のクラス
 */
data class RecruitmentParticipantId(
    val recruitmentId: Long = 0,
    val userId: String = ""
) : java.io.Serializable