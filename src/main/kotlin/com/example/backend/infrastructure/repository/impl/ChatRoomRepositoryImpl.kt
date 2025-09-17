package com.example.backend.infrastructure.repository.impl

import com.example.backend.domain.model.ChatRoom
import com.example.backend.domain.model.Recruitment
import com.example.backend.infrastructure.entity.ChatRoomEntity
import com.example.backend.infrastructure.repository.adapter.jpa.JpaChatRoomRepository
import com.example.backend.infrastructure.repository.adapter.jpa.JpaRecruitmentRepository
import com.example.backend.usecase.gateway.ChatRoomRepositoryPort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

/**
 * チャットルームリポジトリ実装
 */
@Repository
class ChatRoomRepositoryImpl(
    private val jpaChatRoomRepository: JpaChatRoomRepository,
    private val jpaRecruitmentRepository: JpaRecruitmentRepository
) : ChatRoomRepositoryPort {

    /**
     * 募集に紐づくチャットルームを作成する
     *
     * @param recruitment 募集
     * @return 作成されたチャットルーム
     */
    override fun createForRecruitment(recruitment: Recruitment): ChatRoom {
        val recruitmentEntity = jpaRecruitmentRepository.findByIdOrNull(recruitment.id)
            ?: throw IllegalArgumentException("Recruitment not found with id: ${recruitment.id}")

        val chatRoomEntity = ChatRoomEntity(recruitment = recruitmentEntity)
        val savedEntity = jpaChatRoomRepository.save(chatRoomEntity)
        return savedEntity.toDomainModel()
    }

    /**
     * 募集IDからチャットルームが存在するかどうかを確認する
     *
     * @param recruitmentId 募集ID
     * @return 存在すればtrue
     */
    override fun existsByRecruitmentId(recruitmentId: Long): Boolean {
        return jpaChatRoomRepository.existsByRecruitmentId(recruitmentId)
    }

    /**
     * ChatRoomEntityをChatRoomドメインモデルに変換する
     *
     * @return ChatRoomドメインモデル
     */
    private fun ChatRoomEntity.toDomainModel(): ChatRoom {
        return ChatRoom(
            id = this.id,
            recruitmentId = this.recruitment.id,
            participants = this.recruitment.participants.map { it.user.id }
        )
    }

    /**
     * ユーザーがチャットルームの参加者であるかを確認する
     *
     * @param chatRoomId チャットルームID
     * @param userId ユーザーID
     * @return 参加者であればtrue
     */
    override fun isParticipant(chatRoomId: Long, userId: Long): Boolean {
        val chatRoomEntity = jpaChatRoomRepository.findByIdOrNull(chatRoomId) ?: return false
        return chatRoomEntity.recruitment.participants.any { it.user.id == userId }
    }
}
