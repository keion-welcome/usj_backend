package com.example.backend.domain.model

import java.time.LocalDateTime

/**
 * アトラクション ドメインモデル
 *
 * @property id アトラクションの一意なID
 * @property name アトラクション名
 * @property description アトラクションの説明
 * @property waitTime 待ち時間（分）
 * @property isActive アクティブかどうか
 * @property createdAt 作成日時
 * @property updatedAt 更新日時
 */
data class Attraction(
    val id: Long? = null,
    val name: String,
    val description: String? = null,
    val waitTime: Int = 0,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    /**
     * 待ち時間のステータスを取得
     */
    fun getWaitTimeStatus(): WaitTimeStatus {
        return when {
            waitTime <= 30 -> WaitTimeStatus.SHORT
            waitTime <= 60 -> WaitTimeStatus.MEDIUM
            waitTime <= 90 -> WaitTimeStatus.LONG
            else -> WaitTimeStatus.VERY_LONG
        }
    }
}

/**
 * 待ち時間ステータス
 */
enum class WaitTimeStatus {
    SHORT,    // 短い（30分以下）
    MEDIUM,   // 普通（31-60分）
    LONG,     // 長い（61-90分）
    VERY_LONG // とても長い（91分以上）
}