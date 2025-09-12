package com.example.backend.api.dto.response

import com.example.backend.domain.model.User
import com.example.backend.infrastructure.entity.ProfileEntity
import java.time.LocalDateTime

/**
 * ユーザー情報レスポンス
 */
data class UserResponse(
    val id: Long,
    val userId: String?,
    val email: String,
    val nickname: String?,
    val birthDate: String?,
    val gender: String?,
    val hasAnnualPass: Boolean?,
    val area: String?,
    val occupation: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(user: User, profile: ProfileEntity? = null): UserResponse {
            return UserResponse(
                id = user.id!!,
                userId = user.userId,
                email = user.email,
                nickname = profile?.nickname ?: user.email,
                birthDate = profile?.birthdate?.toString(),
                gender = profile?.gender?.name,
                hasAnnualPass = profile?.hasAnnualPass,
                area = profile?.area,
                occupation = profile?.occupation,
                createdAt = null, // UserエンティティにcreatedAtがない場合
                updatedAt = null  // UserエンティティにupdatedAtがない場合
            )
        }
    }
}