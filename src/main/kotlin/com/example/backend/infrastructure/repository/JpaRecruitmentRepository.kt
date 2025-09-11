package com.example.backend.infrastructure.repository

import com.example.backend.infrastructure.entity.RecruitmentEntity
import com.example.backend.infrastructure.entity.RecruitmentStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * 募集リポジトリインターフェース
 */
interface JpaRecruitmentRepository : JpaRepository<RecruitmentEntity, Long> {
    
    /**
     * ステータスで募集を検索
     *
     * @param status 募集ステータス
     * @return 募集リスト
     */
    fun findByStatus(status: RecruitmentStatus): List<RecruitmentEntity>

    /**
     * ユーザーIDで募集を検索
     *
     * @param userId ユーザーID
     * @return 募集リスト
     */
    fun findByUserId(userId: Long): List<RecruitmentEntity>

    /**
     * ユーザーが参加している募集を検索
     *
     * @param userId ユーザーID
     * @return 募集リスト
     */
    @Query("""
        SELECT DISTINCT r FROM RecruitmentEntity r 
        JOIN r.participants p 
        WHERE p.userId = :userId
    """)
    fun findByParticipantUserId(userId: Long): List<RecruitmentEntity>

    /**
     * 作成日時順で募集を取得
     *
     * @return 募集リスト
     */
    fun findAllByOrderByCreatedAtDesc(): List<RecruitmentEntity>

    /**
     * アクティブな募集を作成日時順で取得
     *
     * @return 募集リスト
     */
    fun findByStatusOrderByCreatedAtDesc(status: RecruitmentStatus): List<RecruitmentEntity>

    /**
     * アトラクションIDで募集を検索
     *
     * @param attractionId アトラクションID
     * @return 募集リスト
     */
    fun findByAttractionIdOrderByCreatedAtDesc(attractionId: Long): List<RecruitmentEntity>
}