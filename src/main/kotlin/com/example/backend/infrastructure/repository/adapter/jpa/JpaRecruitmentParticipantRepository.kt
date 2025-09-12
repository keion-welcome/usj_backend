package com.example.backend.infrastructure.repository.adapter.jpa

import com.example.backend.infrastructure.entity.RecruitmentParticipantEntity
import com.example.backend.infrastructure.entity.RecruitmentParticipantId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * 募集参加者リポジトリインターフェース
 */
interface JpaRecruitmentParticipantRepository : JpaRepository<RecruitmentParticipantEntity, RecruitmentParticipantId> {
    
    /**
     * 募集IDで参加者を検索
     *
     * @param recruitmentId 募集ID
     * @return 参加者リスト
     */
    fun findByRecruitmentId(recruitmentId: Long): List<RecruitmentParticipantEntity>

    /**
     * ユーザーIDで参加している募集の参加者情報を検索
     *
     * @param userId ユーザーID
     * @return 参加者リスト
     */
    fun findByUserId(userId: String): List<RecruitmentParticipantEntity>

    /**
     * 募集IDとユーザーIDで参加者を検索
     *
     * @param recruitmentId 募集ID
     * @param userId ユーザーID
     * @return 参加者情報、存在しない場合はnull
     */
    fun findByRecruitmentIdAndUserId(recruitmentId: Long, userId: String): RecruitmentParticipantEntity?

    /**
     * 募集IDとユーザーIDで参加者を削除
     *
     * @param recruitmentId 募集ID
     * @param userId ユーザーID
     */
    fun deleteByRecruitmentIdAndUserId(recruitmentId: Long, userId: String)

    /**
     * 募集IDで参加者数をカウント
     *
     * @param recruitmentId 募集ID
     * @return 参加者数
     */
    fun countByRecruitmentId(recruitmentId: Long): Int
}