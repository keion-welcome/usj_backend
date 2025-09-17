package com.example.backend.usecase.gateway

import com.example.backend.domain.model.ChatRoom
import com.example.backend.domain.model.Recruitment

/**
 * チャットルームリポジトリポート
 */
interface ChatRoomRepositoryPort {
    /**
     * 募集に紐づくチャットルームを作成する
     *
     * @param recruitment 募集
     * @return 作成されたチャットルーム
     */
    fun createForRecruitment(recruitment: Recruitment): ChatRoom

    /**
     * 募集IDからチャットルームが存在するかどうかを確認する
     *
     * @param recruitmentId 募集ID
     * @return 存在すればtrue
     */
    fun existsByRecruitmentId(recruitmentId: Long): Boolean

    /**
     * ユーザーがチャットルームの参加者であるかを確認する
     *
     * @param chatRoomId チャットルームID
     * @param userId ユーザーID
     * @return 参加者であればtrue
     */
    fun isParticipant(chatRoomId: Long, userId: Long): Boolean
}
