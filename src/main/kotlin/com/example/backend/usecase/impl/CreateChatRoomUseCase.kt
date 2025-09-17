package com.example.backend.usecase.impl

import com.example.backend.domain.model.ChatRoom
import com.example.backend.domain.model.Recruitment
import com.example.backend.usecase.gateway.ChatRoomRepositoryPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * チャットルーム作成ユースケース
 */
interface CreateChatRoomUseCase {
    /**
     * 募集に紐づくチャットルームを作成する
     *
     * @param recruitment 募集
     * @return 作成されたチャットルーム。既に存在していた場合はnull
     */
    fun createForRecruitment(recruitment: Recruitment): ChatRoom?
}

@Service
class CreateChatRoomUseCaseImpl(
    private val chatRoomRepository: ChatRoomRepositoryPort
) : CreateChatRoomUseCase {

    private val logger = LoggerFactory.getLogger(CreateChatRoomUseCaseImpl::class.java)

    /**
     * 募集に紐づくチャットルームを作成する
     *
     * @param recruitment 募集
     * @return 作成されたチャットルーム。既に存在していた場合はnull
     */
    @Transactional
    override fun createForRecruitment(recruitment: Recruitment): ChatRoom? {
        // 既にチャットルームが存在しないか確認
        if (chatRoomRepository.existsByRecruitmentId(recruitment.id)) {
            logger.warn("Chat room already exists for recruitment id: ${recruitment.id}")
            return null
        }

        // チャットルームを作成
        val chatRoom = chatRoomRepository.createForRecruitment(recruitment)
        logger.info("Created chat room id: ${chatRoom.id} for recruitment id: ${recruitment.id}")
        return chatRoom
    }
}
