package com.example.backend.infrastructure.repository.adapter.jpa

import com.example.backend.infrastructure.entity.ChatRoomEntity
import org.springframework.data.jpa.repository.JpaRepository

/**
 * チャットルームJPAリポジトリ
 */
interface JpaChatRoomRepository : JpaRepository<ChatRoomEntity, Long> {
    /**
     * 募集IDからチャットルームが存在するかどうかを確認する
     *
     * @param recruitmentId 募集ID
     * @return 存在すればtrue
     */
    fun existsByRecruitmentId(recruitmentId: Long): Boolean
}
