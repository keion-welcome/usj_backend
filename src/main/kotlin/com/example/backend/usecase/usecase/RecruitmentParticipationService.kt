package com.example.backend.usecase.usecase

import com.example.backend.api.controller.WebSocketController
import com.example.backend.domain.model.ChatRoom
import com.example.backend.domain.model.Recruitment
import com.example.backend.domain.model.RecruitmentStatus
import com.example.backend.usecase.gateway.RecruitmentRepositoryPort
import com.example.backend.usecase.impl.CreateChatRoomUseCase
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

/**
 * 募集参加サービス
 */
interface RecruitmentParticipationService {
    /**
     * 募集に参加する
     *
     * @param recruitmentId 募集ID
     * @param userId ユーザーID
     * @return 参加に成功した場合は更新された募集、失敗した場合はnull
     */
    fun joinRecruitment(recruitmentId: Long, userId: Long): Recruitment?

    /**
     * 募集から退出する
     *
     * @param recruitmentId 募集ID
     * @param userId ユーザーID
     * @return 退出に成功した場合は更新された募集、失敗した場合はnull
     */
    fun leaveRecruitment(recruitmentId: Long, userId: Long): Recruitment?
}

/**
 * 募集参加サービス実装
 *
 * @property recruitmentRepositoryPort 募集リポジトリポート
 */
@Service
class RecruitmentParticipationServiceImpl(
    private val recruitmentRepositoryPort: RecruitmentRepositoryPort,
    private val createChatRoomUseCase: CreateChatRoomUseCase,
    @Lazy private val webSocketController: WebSocketController // 循環参照を避けるために@Lazyを使用
) : RecruitmentParticipationService {

    override fun joinRecruitment(recruitmentId: Long, userId: Long): Recruitment? {
        val recruitment = recruitmentRepositoryPort.findById(recruitmentId) ?: return null

        // 募集がアクティブかチェック
        if (recruitment.status != RecruitmentStatus.ACTIVE) {
            throw IllegalStateException("この募集は現在参加できません")
        }

        // 募集が満員かチェック
        if (recruitment.isFull()) {
            throw IllegalStateException("募集は満員です")
        }

        // 既に参加しているかチェック
        if (recruitment.isParticipating(userId)) {
            throw IllegalStateException("既に参加しています")
        }

        // 自分の募集には参加できない
        if (recruitment.userId == userId) {
            throw IllegalStateException("自分の募集には参加できません")
        }

        // 参加処理
        val success = recruitmentRepositoryPort.joinRecruitment(recruitmentId, userId)
        if (!success) {
            return null
        }

        val updatedRecruitment = recruitmentRepositoryPort.findById(recruitmentId) ?: return null

        // 満員になったかチェックし、チャットルームを作成
        if (updatedRecruitment.isFull()) {
            val chatRoom = createChatRoomUseCase.createForRecruitment(updatedRecruitment)
            if (chatRoom != null) {
                // 参加者にチャットルーム作成を通知
                webSocketController.notifyChatRoomCreated(chatRoom)
            }
        }

        return updatedRecruitment
    }

    override fun leaveRecruitment(recruitmentId: Long, userId: Long): Recruitment? {
        val recruitment = recruitmentRepositoryPort.findById(recruitmentId) ?: return null

        // 参加しているかチェック
        if (!recruitment.isParticipating(userId)) {
            throw IllegalStateException("この募集に参加していません")
        }

        // 退出処理
        val success = recruitmentRepositoryPort.leaveRecruitment(recruitmentId, userId)
        return if (success) {
            recruitmentRepositoryPort.findById(recruitmentId)
        } else {
            null
        }
    }
}