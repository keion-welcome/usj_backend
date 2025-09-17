package com.example.backend.usecase.impl

import com.example.backend.api.dto.request.SendChatMessageRequest
import com.example.backend.domain.model.ChatMessage
import com.example.backend.domain.model.Profile
import com.example.backend.usecase.gateway.ChatRoomRepositoryPort
import com.example.backend.usecase.gateway.ProfileRepositoryPort
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * チャットメッセージ送信ユースケース
 */
interface SendChatMessageUseCase {
    fun execute(request: SendChatMessageRequest, userId: Long): ChatMessage
}

@Service
class SendChatMessageUseCaseImpl(
    private val chatRoomRepository: ChatRoomRepositoryPort,
    private val profileRepository: ProfileRepositoryPort
) : SendChatMessageUseCase {

    override fun execute(request: SendChatMessageRequest, userId: Long): ChatMessage {
        // 1. ユーザーがチャットルームの参加者か検証
        if (!chatRoomRepository.isParticipant(request.chatRoomId, userId)) {
            throw SecurityException("User $userId is not a participant of chat room ${request.chatRoomId}")
        }

        // 2. 送信者のプロフィール情報を取得
        val userProfile = profileRepository.findByUserId(userId)
            ?: throw IllegalStateException("Profile not found for user $userId")

        // 3. ChatMessageドメインモデルを生成
        // メッセージの永続化はまだ実装しないため、IDはダミー値
        return ChatMessage(
            id = System.currentTimeMillis(),
            chatRoomId = request.chatRoomId,
            userId = userId,
            userNickname = userProfile.nickname,
            userIconUrl = userProfile.iconUrl,
            content = request.content,
            timestamp = LocalDateTime.now()
        )
    }
}
