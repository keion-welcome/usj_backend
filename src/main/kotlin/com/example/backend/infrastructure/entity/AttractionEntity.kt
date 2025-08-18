package com.example.backend.infrastructure.entity

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * アトラクションエンティティ
 *
 * @property id アトラクションの一意なID
 * @property name アトラクション名
 * @property description アトラクションの説明
 * @property waitTime 待ち時間（分）
 * @property isActive アクティブかどうか
 * @property createdAt 作成日時
 * @property updatedAt 更新日時
 */
@Entity
@Table(name = "attractions")
data class AttractionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val name: String,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @Column(name = "wait_time", nullable = false)
    val waitTime: Int = 0,

    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = null
)