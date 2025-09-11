package com.example.backend.usecase.gateway

import com.example.backend.domain.model.Recruitment
import com.example.backend.domain.model.RecruitmentStatus

/**
 * 募集リポジトリポート
 */
interface RecruitmentRepositoryPort {
    /**
     * 募集を作成する
     *
     * @param recruitment 作成する募集
     * @return 作成された募集
     */
    fun create(recruitment: Recruitment): Recruitment

    /**
     * 募集を更新する
     *
     * @param recruitment 更新する募集
     * @return 更新された募集
     */
    fun update(recruitment: Recruitment): Recruitment

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
     * ステータスで募集を検索する
     *
     * @param status 募集ステータス
     * @return 該当する募集のリスト
     */
    fun findByStatus(status: RecruitmentStatus): List<Recruitment>

    /**
     * ユーザーが作成した募集を取得する
     *
     * @param userId ユーザーID
     * @return 該当する募集のリスト
     */
    fun findByUserId(userId: Long): List<Recruitment>

    /**
     * ユーザーが参加している募集を取得する
     *
     * @param userId ユーザーID
     * @return 該当する募集のリスト
     */
    fun findByParticipantUserId(userId: Long): List<Recruitment>

    /**
     * アトラクションに関連する募集を取得する
     *
     * @param attractionId アトラクションID
     * @return 該当する募集のリスト
     */
    fun findByAttractionId(attractionId: Long): List<Recruitment>

    /**
     * 募集に参加する
     *
     * @param recruitmentId 募集ID
     * @param userId ユーザーID
     * @return 成功した場合はtrue
     */
    fun joinRecruitment(recruitmentId: Long, userId: Long): Boolean

    /**
     * 募集から退出する
     *
     * @param recruitmentId 募集ID
     * @param userId ユーザーID
     * @return 成功した場合はtrue
     */
    fun leaveRecruitment(recruitmentId: Long, userId: Long): Boolean

    /**
     * 募集を削除する
     *
     * @param id 募集ID
     * @return 削除できた場合はtrue
     */
    fun deleteById(id: Long): Boolean
}