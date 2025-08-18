package com.example.backend.api.controller

import com.example.backend.api.dto.request.CreateRecruitmentRequest
import com.example.backend.api.dto.request.JoinRecruitmentRequest
import com.example.backend.api.dto.request.LeaveRecruitmentRequest
import com.example.backend.api.dto.request.UpdateRecruitmentRequest
import com.example.backend.api.dto.response.*
import com.example.backend.domain.model.RecruitmentStatus
import com.example.backend.usecase.usecase.CreateRecruitmentUseCase
import com.example.backend.usecase.usecase.RecruitmentService
import com.example.backend.usecase.usecase.RecruitmentParticipationService
import com.example.backend.usecase.gateway.UserRepositoryPort
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

/**
 * 募集コントローラー
 * 
 * 募集機能のREST APIエンドポイントを提供する
 */
@RestController
@RequestMapping("/api/recruitments")
class RecruitmentController(
    private val createRecruitmentUseCase: CreateRecruitmentUseCase,
    private val recruitmentService: RecruitmentService,
    private val recruitmentParticipationService: RecruitmentParticipationService,
    private val userRepositoryPort: UserRepositoryPort,
    private val messagingTemplate: SimpMessagingTemplate,
    private val webSocketController: WebSocketController
) {

    /**
     * 募集を作成する
     *
     * @param request 募集作成リクエスト
     * @param userDetails 認証ユーザー情報
     * @return 募集レスポンス
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createRecruitment(
        @RequestBody request: CreateRecruitmentRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): RecruitmentResponse {
        val email = userDetails.username
        val user = userRepositoryPort.findByEmail(email) ?: throw Exception("User not found")
        val userId = user.id!!
        
        val recruitment = createRecruitmentUseCase.create(
            title = request.title,
            description = request.description,
            userId = userId,
            attractionId = request.attractionId,
            maxParticipants = request.maxParticipants,
            expiresAt = request.expiresAt
        )
        
        val response = RecruitmentResponse.from(recruitment)
        
        // WebSocket通知
        webSocketController.notifyRecruitmentUpdate(
            recruitmentId = recruitment.id!!,
            messageType = WebSocketMessageType.RECRUITMENT_UPDATED,
            data = response
        )
        
        return response
    }

    /**
     * すべての募集を取得する
     *
     * @return 募集リスト
     */
    @GetMapping
    fun getAllRecruitments(): List<RecruitmentResponse> {
        return recruitmentService.findAll()
            .map { RecruitmentResponse.from(it) }
    }

    /**
     * アクティブな募集のみを取得する
     *
     * @return アクティブな募集リスト
     */
    @GetMapping("/active")
    fun getActiveRecruitments(): List<RecruitmentResponse> {
        return recruitmentService.findActiveRecruitments()
            .map { RecruitmentResponse.from(it) }
    }

    /**
     * 募集をIDで取得する
     *
     * @param id 募集ID
     * @return 募集レスポンス
     */
    @GetMapping("/{id}")
    fun getRecruitmentById(@PathVariable id: Long): RecruitmentResponse {
        val recruitment = recruitmentService.findById(id)
            ?: throw Exception("Recruitment not found")
        return RecruitmentResponse.from(recruitment)
    }

    /**
     * 自分が作成した募集を取得する
     *
     * @param userDetails 認証ユーザー情報
     * @return 募集リスト
     */
    @GetMapping("/my")
    fun getMyRecruitments(@AuthenticationPrincipal userDetails: UserDetails): List<RecruitmentResponse> {
        val email = userDetails.username
        val user = userRepositoryPort.findByEmail(email) ?: throw Exception("User not found")
        val userId = user.id!!
        
        return recruitmentService.findByUserId(userId)
            .map { RecruitmentResponse.from(it) }
    }

    /**
     * 自分が参加している募集を取得する
     *
     * @param userDetails 認証ユーザー情報
     * @return 募集リスト
     */
    @GetMapping("/participating")
    fun getParticipatingRecruitments(@AuthenticationPrincipal userDetails: UserDetails): List<RecruitmentResponse> {
        val email = userDetails.username
        val user = userRepositoryPort.findByEmail(email) ?: throw Exception("User not found")
        val userId = user.id!!
        
        return recruitmentService.findByParticipantUserId(userId)
            .map { RecruitmentResponse.from(it) }
    }

    /**
     * アトラクション別の募集を取得する
     *
     * @param attractionId アトラクションID
     * @return 募集リスト
     */
    @GetMapping("/attraction/{attractionId}")
    fun getRecruitmentsByAttraction(@PathVariable attractionId: Long): List<RecruitmentResponse> {
        return recruitmentService.findByAttractionId(attractionId)
            .map { RecruitmentResponse.from(it) }
    }

    /**
     * 募集に参加する（REST API版）
     *
     * @param request 参加リクエスト
     * @param userDetails 認証ユーザー情報
     * @return 更新された募集レスポンス
     */
    @PostMapping("/join")
    fun joinRecruitment(
        @RequestBody request: JoinRecruitmentRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): RecruitmentResponse {
        val email = userDetails.username
        val user = userRepositoryPort.findByEmail(email) ?: throw Exception("User not found")
        val userId = user.id!!
        
        val updatedRecruitment = recruitmentParticipationService.joinRecruitment(request.recruitmentId, userId)
            ?: throw Exception("Failed to join recruitment")
        
        val response = RecruitmentResponse.from(updatedRecruitment)
        
        // WebSocket通知
        val message = ParticipantJoinedMessage(
            userId = userId,
            currentParticipants = updatedRecruitment.participants.size,
            maxParticipants = updatedRecruitment.maxParticipants,
            isFull = updatedRecruitment.isFull()
        )
        
        webSocketController.notifyRecruitmentUpdate(
            recruitmentId = request.recruitmentId,
            messageType = WebSocketMessageType.PARTICIPANT_JOINED,
            data = message
        )
        
        return response
    }

    /**
     * 募集から退出する（REST API版）
     *
     * @param request 退出リクエスト
     * @param userDetails 認証ユーザー情報
     * @return 更新された募集レスポンス
     */
    @PostMapping("/leave")
    fun leaveRecruitment(
        @RequestBody request: LeaveRecruitmentRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): RecruitmentResponse {
        val email = userDetails.username
        val user = userRepositoryPort.findByEmail(email) ?: throw Exception("User not found")
        val userId = user.id!!
        
        val updatedRecruitment = recruitmentParticipationService.leaveRecruitment(request.recruitmentId, userId)
            ?: throw Exception("Failed to leave recruitment")
        
        val response = RecruitmentResponse.from(updatedRecruitment)
        
        // WebSocket通知
        val message = ParticipantLeftMessage(
            userId = userId,
            currentParticipants = updatedRecruitment.participants.size,
            maxParticipants = updatedRecruitment.maxParticipants,
            isFull = updatedRecruitment.isFull()
        )
        
        webSocketController.notifyRecruitmentUpdate(
            recruitmentId = request.recruitmentId,
            messageType = WebSocketMessageType.PARTICIPANT_LEFT,
            data = message
        )
        
        return response
    }

    /**
     * 募集を更新する
     *
     * @param request 更新リクエスト
     * @param userDetails 認証ユーザー情報
     * @return 更新された募集レスポンス
     */
    @PutMapping("/{id}")
    fun updateRecruitment(
        @PathVariable id: Long,
        @RequestBody request: UpdateRecruitmentRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): RecruitmentResponse {
        val email = userDetails.username
        val user = userRepositoryPort.findByEmail(email) ?: throw Exception("User not found")
        val userId = user.id!!
        
        val existingRecruitment = recruitmentService.findById(id)
            ?: throw Exception("Recruitment not found")
        
        if (existingRecruitment.userId != userId) {
            throw Exception("You don't have permission to update this recruitment")
        }
        
        val updatedRecruitment = existingRecruitment.copy(
            title = request.title,
            description = request.description
        )
        
        val savedRecruitment = recruitmentService.updateRecruitment(updatedRecruitment)
        val response = RecruitmentResponse.from(savedRecruitment)
        
        // WebSocket通知
        webSocketController.notifyRecruitmentUpdate(
            recruitmentId = id,
            messageType = WebSocketMessageType.RECRUITMENT_UPDATED,
            data = response
        )
        
        return response
    }

    /**
     * 募集をキャンセルする
     *
     * @param id 募集ID
     * @param userDetails 認証ユーザー情報
     * @return 更新された募集レスポンス
     */
    @PostMapping("/{id}/cancel")
    fun cancelRecruitment(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: UserDetails
    ): RecruitmentResponse {
        val email = userDetails.username
        val user = userRepositoryPort.findByEmail(email) ?: throw Exception("User not found")
        val userId = user.id!!
        
        val updatedRecruitment = recruitmentService.cancelRecruitment(id, userId)
            ?: throw Exception("Failed to cancel recruitment")
        
        val response = RecruitmentResponse.from(updatedRecruitment)
        
        // WebSocket通知
        val message = RecruitmentStatusChangedMessage(
            status = RecruitmentStatus.CANCELLED,
            reason = "募集がキャンセルされました"
        )
        
        webSocketController.notifyRecruitmentUpdate(
            recruitmentId = id,
            messageType = WebSocketMessageType.RECRUITMENT_CANCELLED,
            data = message
        )
        
        return response
    }

    /**
     * 募集を完了する
     *
     * @param id 募集ID
     * @param userDetails 認証ユーザー情報
     * @return 更新された募集レスポンス
     */
    @PostMapping("/{id}/complete")
    fun completeRecruitment(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: UserDetails
    ): RecruitmentResponse {
        val email = userDetails.username
        val user = userRepositoryPort.findByEmail(email) ?: throw Exception("User not found")
        val userId = user.id!!
        
        val updatedRecruitment = recruitmentService.completeRecruitment(id, userId)
            ?: throw Exception("Failed to complete recruitment")
        
        val response = RecruitmentResponse.from(updatedRecruitment)
        
        // WebSocket通知
        val message = RecruitmentStatusChangedMessage(
            status = RecruitmentStatus.COMPLETED,
            reason = "募集が完了しました"
        )
        
        webSocketController.notifyRecruitmentUpdate(
            recruitmentId = id,
            messageType = WebSocketMessageType.RECRUITMENT_COMPLETED,
            data = message
        )
        
        return response
    }

    /**
     * 募集を削除する
     *
     * @param id 募集ID
     * @param userDetails 認証ユーザー情報
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteRecruitment(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: UserDetails
    ) {
        val email = userDetails.username
        val user = userRepositoryPort.findByEmail(email) ?: throw Exception("User not found")
        val userId = user.id!!
        
        val success = recruitmentService.deleteRecruitment(id, userId)
        if (!success) {
            throw Exception("Failed to delete recruitment")
        }
        
        // WebSocket通知 - 削除通知
        webSocketController.notifyRecruitmentUpdate(
            recruitmentId = id,
            messageType = WebSocketMessageType.RECRUITMENT_CANCELLED,
            data = RecruitmentStatusChangedMessage(
                status = RecruitmentStatus.CANCELLED,
                reason = "募集が削除されました"
            )
        )
    }
}
