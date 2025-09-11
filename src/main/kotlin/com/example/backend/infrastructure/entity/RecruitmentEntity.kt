package com.example.backend.infrastructure.entity

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 募集エンティティ
 *
 * @property id 募集の一意なID
 * @property title 募集のタイトル
 * @property description 募集の詳細な説明
 * @property userId 募集を作成したユーザーのID
 * @property maxParticipants 最大参加者数
 * @property status 募集ステータス
 * @property expiresAt 募集締切日時
 * @property participants 参加者リスト
 * @property createdAt 募集の作成日時
 * @property updatedAt 募集の最終更新日時
 */
@Entity
@Table(name = "recruitments")
data class RecruitmentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val description: String,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "max_participants", nullable = false)
    val maxParticipants: Int = 4,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: RecruitmentStatus = RecruitmentStatus.ACTIVE,

    @Column(name = "expires_at")
    val expiresAt: LocalDateTime? = null,

    @Column(name = "attraction_id")
    val attractionId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attraction_id", insertable = false, updatable = false)
    val attraction: AttractionEntity? = null,

    // 参加者リスト
    @OneToMany(mappedBy = "recruitment", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val participants: MutableSet<RecruitmentParticipantEntity> = mutableSetOf(),

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = null
)

enum class RecruitmentStatus {
    ACTIVE,
    COMPLETED,
    CANCELLED
}
