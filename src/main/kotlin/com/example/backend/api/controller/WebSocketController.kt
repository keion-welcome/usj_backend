package com.example.backend.api.controller

import com.example.backend.api.dto.request.JoinRecruitmentRequest
import com.example.backend.api.dto.request.LeaveRecruitmentRequest
import com.example.backend.api.dto.response.*
import com.example.backend.usecase.usecase.RecruitmentParticipationService
import com.example.backend.usecase.usecase.RecruitmentService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller
import java.security.Principal

/**
 * WebSocketコントローラー
 * 
 * 募集機能のリアルタイム通信を処理する
 */
@Controller
class WebSocketController(
    private val recruitmentService: RecruitmentService,
    private val recruitmentParticipationService: RecruitmentParticipationService,
    private val messagingTemplate: SimpMessagingTemplate
) {

    /**
     * 募集に参加する
     * 
     * @param request 参加リクエスト
     * @param principal 認証情報
     */
    @MessageMapping("/recruitment/join")
    fun joinRecruitment(@Payload request: JoinRecruitmentRequest, principal: Principal) {
        try {
            val userId = principal.name.toLong() // JWTトークンからユーザーIDを取得
            val updatedRecruitment = recruitmentParticipationService.joinRecruitment(request.recruitmentId, userId)
            
            if (updatedRecruitment != null) {
                // 参加成功メッセージを特定の募集を購読している全ユーザーに送信
                val message = ParticipantJoinedMessage(
                    userId = userId,
                    currentParticipants = updatedRecruitment.participants.size,
                    maxParticipants = updatedRecruitment.maxParticipants,
                    isFull = updatedRecruitment.isFull()
                )
                
                val response = WebSocketResponse(
                    type = WebSocketMessageType.PARTICIPANT_JOINED,
                    recruitmentId = request.recruitmentId,
                    data = message
                )
                
                messagingTemplate.convertAndSend("/topic/recruitment/${request.recruitmentId}", response)
                
                // 全体の募集リストの更新も通知
                messagingTemplate.convertAndSend("/topic/recruitments", 
                    WebSocketResponse(
                        type = WebSocketMessageType.RECRUITMENT_UPDATED,
                        recruitmentId = request.recruitmentId,
                        data = RecruitmentResponse.from(updatedRecruitment)
                    )
                )
            } else {
                sendErrorToUser(principal.name, "募集への参加に失敗しました", "JOIN_FAILED")
            }
        } catch (e: Exception) {
            sendErrorToUser(principal.name, e.message ?: "募集への参加中にエラーが発生しました", "JOIN_ERROR")
        }
    }

    /**
     * 募集から退出する
     * 
     * @param request 退出リクエスト
     * @param principal 認証情報
     */
    @MessageMapping("/recruitment/leave")
    fun leaveRecruitment(@Payload request: LeaveRecruitmentRequest, principal: Principal) {
        try {
            val userId = principal.name.toLong()
            val updatedRecruitment = recruitmentParticipationService.leaveRecruitment(request.recruitmentId, userId)
            
            if (updatedRecruitment != null) {
                // 退出成功メッセージを特定の募集を購読している全ユーザーに送信
                val message = ParticipantLeftMessage(
                    userId = userId,
                    currentParticipants = updatedRecruitment.participants.size,
                    maxParticipants = updatedRecruitment.maxParticipants,
                    isFull = updatedRecruitment.isFull()
                )
                
                val response = WebSocketResponse(
                    type = WebSocketMessageType.PARTICIPANT_LEFT,
                    recruitmentId = request.recruitmentId,
                    data = message
                )
                
                messagingTemplate.convertAndSend("/topic/recruitment/${request.recruitmentId}", response)
                
                // 全体の募集リストの更新も通知
                messagingTemplate.convertAndSend("/topic/recruitments", 
                    WebSocketResponse(
                        type = WebSocketMessageType.RECRUITMENT_UPDATED,
                        recruitmentId = request.recruitmentId,
                        data = RecruitmentResponse.from(updatedRecruitment)
                    )
                )
            } else {
                sendErrorToUser(principal.name, "募集からの退出に失敗しました", "LEAVE_FAILED")
            }
        } catch (e: Exception) {
            sendErrorToUser(principal.name, e.message ?: "募集からの退出中にエラーが発生しました", "LEAVE_ERROR")
        }
    }

    /**
     * 特定の募集の状態を取得する
     * 
     * @param recruitmentId 募集ID
     * @param principal 認証情報
     * @return 募集の状態
     */
    @MessageMapping("/recruitment/status")
    @SendToUser("/queue/recruitment/status")
    fun getRecruitmentStatus(@Payload recruitmentId: Long, principal: Principal): WebSocketResponse {
        return try {
            val recruitment = recruitmentService.findById(recruitmentId)
            if (recruitment != null) {
                WebSocketResponse(
                    type = WebSocketMessageType.RECRUITMENT_UPDATED,
                    recruitmentId = recruitmentId,
                    data = RecruitmentResponse.from(recruitment)
                )
            } else {
                WebSocketResponse(
                    type = WebSocketMessageType.ERROR,
                    recruitmentId = recruitmentId,
                    data = ErrorMessage("募集が見つかりません", "NOT_FOUND")
                )
            }
        } catch (e: Exception) {
            WebSocketResponse(
                type = WebSocketMessageType.ERROR,
                recruitmentId = recruitmentId,
                data = ErrorMessage(e.message ?: "エラーが発生しました", "UNKNOWN_ERROR")
            )
        }
    }

    /**
     * ユーザーにエラーメッセージを送信する
     * 
     * @param username ユーザー名
     * @param message エラーメッセージ
     * @param code エラーコード
     */
    private fun sendErrorToUser(username: String, message: String, code: String) {
        val errorResponse = WebSocketResponse(
            type = WebSocketMessageType.ERROR,
            recruitmentId = 0,
            data = ErrorMessage(message, code)
        )
        messagingTemplate.convertAndSendToUser(username, "/queue/errors", errorResponse)
    }

    /**
     * 募集の状態変更を通知する（他のサービスから呼び出される）
     * 
     * @param recruitmentId 募集ID
     * @param messageType メッセージタイプ
     * @param data メッセージデータ
     */
    fun notifyRecruitmentUpdate(recruitmentId: Long, messageType: WebSocketMessageType, data: Any) {
        val response = WebSocketResponse(
            type = messageType,
            recruitmentId = recruitmentId,
            data = data
        )
        
        // 特定の募集を購読しているユーザーに通知
        messagingTemplate.convertAndSend("/topic/recruitment/$recruitmentId", response)
        
        // 全体の募集リストにも通知（リストビューの更新用）
        messagingTemplate.convertAndSend("/topic/recruitments", response)
    }
}