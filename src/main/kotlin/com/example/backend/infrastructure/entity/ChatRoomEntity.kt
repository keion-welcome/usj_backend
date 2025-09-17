package com.example.backend.infrastructure.entity

import jakarta.persistence.*

/**
 * チャットルーム エンティティ
 *
 * @property id チャットルームID
 * @property recruitment 紐づく募集エンティティ
 */
@Entity
@Table(name = "chat_rooms")
class ChatRoomEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id", referencedColumnName = "id", unique = true)
    val recruitment: RecruitmentEntity
) : BaseEntity()
