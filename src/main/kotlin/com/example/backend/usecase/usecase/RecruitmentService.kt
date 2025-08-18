package com.example.backend.usecase.usecase

import com.example.backend.domain.model.Recruitment
import com.example.backend.domain.model.RecruitmentStatus
import com.example.backend.usecase.gateway.RecruitmentRepositoryPort
import org.springframework.stereotype.Service

/**
 * 募集サービス
 */
interface RecruitmentService {
    /**
     * 募集をIDで取得する
     *
     * @param id 募集ID
     * @return 募集、存在しない場合はnull
     */
    fun findById(id: Long): Recruitment?

    /**
     * すべての募集を取得する
     *
     * @return 募集のリスト
     */
    fun findAll(): List<Recruitment>

    /**
     * アクティブな募集を取得する
     *
     * @return 募集のリスト
     */
    fun findActiveRecruitments(): List<Recruitment>

    /**
     * ユーザーが作成した募集を取得する
     *
     * @param userId ユーザーID
     * @return 募集のリスト
     */
    fun findByUserId(userId: Long): List<Recruitment>

    /**
     * ユーザーが参加している募集を取得する
     *
     * @param userId ユーザーID
     * @return 募集のリスト
     */
    fun findByParticipantUserId(userId: Long): List<Recruitment>

    /**
     * アトラクションに関連する募集を取得する
     *
     * @param attractionId アトラクションID
     * @return 募集のリスト
     */
    fun findByAttractionId(attractionId: Long): List<Recruitment>

    /**
     * 募集を更新する
     *
     * @param recruitment 更新する募集
     * @return 更新された募集
     */
    fun updateRecruitment(recruitment: Recruitment): Recruitment

    /**
     * 募集を削除する
     *
     * @param id 募集ID
     * @param userId 削除を要求するユーザーID（作成者のみ削除可能）
     * @return 削除できた場合はtrue
     */
    fun deleteRecruitment(id: Long, userId: Long): Boolean

    /**
     * 募集をキャンセルする
     *
     * @param id 募集ID
     * @param userId キャンセルを要求するユーザーID（作成者のみキャンセル可能）
     * @return 更新された募集
     */
    fun cancelRecruitment(id: Long, userId: Long): Recruitment?

    /**
     * 募集を完了にする
     *
     * @param id 募集ID
     * @param userId 完了を要求するユーザーID（作成者のみ完了可能）
     * @return 更新された募集
     */
    fun completeRecruitment(id: Long, userId: Long): Recruitment?
}

/**
 * 募集サービス実装
 *
 * @property recruitmentRepositoryPort 募集リポジトリポート
 */
@Service
class RecruitmentServiceImpl(
    private val recruitmentRepositoryPort: RecruitmentRepositoryPort
) : RecruitmentService {

    override fun findById(id: Long): Recruitment? {
        return recruitmentRepositoryPort.findById(id)
    }

    override fun findAll(): List<Recruitment> {
        return recruitmentRepositoryPort.findAll()
    }

    override fun findActiveRecruitments(): List<Recruitment> {
        return recruitmentRepositoryPort.findByStatus(RecruitmentStatus.ACTIVE)
    }

    override fun findByUserId(userId: Long): List<Recruitment> {
        return recruitmentRepositoryPort.findByUserId(userId)
    }

    override fun findByParticipantUserId(userId: Long): List<Recruitment> {
        return recruitmentRepositoryPort.findByParticipantUserId(userId)
    }

    override fun findByAttractionId(attractionId: Long): List<Recruitment> {
        return recruitmentRepositoryPort.findByAttractionId(attractionId)
    }

    override fun updateRecruitment(recruitment: Recruitment): Recruitment {
        return recruitmentRepositoryPort.update(recruitment)
    }

    override fun deleteRecruitment(id: Long, userId: Long): Boolean {
        val recruitment = recruitmentRepositoryPort.findById(id) ?: return false
        
        // 作成者のみ削除可能
        if (recruitment.userId != userId) {
            throw IllegalArgumentException("募集を削除する権限がありません")
        }

        return recruitmentRepositoryPort.deleteById(id)
    }

    override fun cancelRecruitment(id: Long, userId: Long): Recruitment? {
        val recruitment = recruitmentRepositoryPort.findById(id) ?: return null
        
        // 作成者のみキャンセル可能
        if (recruitment.userId != userId) {
            throw IllegalArgumentException("募集をキャンセルする権限がありません")
        }

        val updatedRecruitment = recruitment.copy(status = RecruitmentStatus.CANCELLED)
        return recruitmentRepositoryPort.update(updatedRecruitment)
    }

    override fun completeRecruitment(id: Long, userId: Long): Recruitment? {
        val recruitment = recruitmentRepositoryPort.findById(id) ?: return null
        
        // 作成者のみ完了可能
        if (recruitment.userId != userId) {
            throw IllegalArgumentException("募集を完了する権限がありません")
        }

        val updatedRecruitment = recruitment.copy(status = RecruitmentStatus.COMPLETED)
        return recruitmentRepositoryPort.update(updatedRecruitment)
    }
}