package com.example.backend.api.dto.response

import com.example.backend.domain.model.Attraction
import com.example.backend.domain.model.WaitTimeStatus
import java.time.LocalDateTime

/**
 * アトラクションレスポンス
 *
 * @property id アトラクションの一意なID
 * @property name アトラクション名
 * @property description アトラクションの説明
 * @property waitTime 待ち時間（分）
 * @property waitTimeStatus 待ち時間のステータス
 * @property isActive アクティブかどうか
 * @property createdAt 作成日時
 * @property updatedAt 更新日時
 */
data class AttractionResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val waitTime: Int,
    val waitTimeStatus: WaitTimeStatus,
    val isActive: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(attraction: Attraction): AttractionResponse {
            return AttractionResponse(
                id = attraction.id!!,
                name = attraction.name,
                description = attraction.description,
                waitTime = attraction.waitTime,
                waitTimeStatus = attraction.getWaitTimeStatus(),
                isActive = attraction.isActive,
                createdAt = attraction.createdAt,
                updatedAt = attraction.updatedAt
            )
        }
    }
}