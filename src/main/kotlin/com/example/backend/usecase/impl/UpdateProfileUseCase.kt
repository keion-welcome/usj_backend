package com.example.backend.usecase.impl

import com.example.backend.domain.model.Profile
import com.example.backend.usecase.gateway.ProfileRepositoryPort
import com.example.backend.usecase.gateway.UserRepositoryPort
import org.springframework.stereotype.Service

/**
 * プロフィール更新ユースケース
 *
 * @property profileRepositoryPort プロフィールリポジトリのポート
 * @property userRepositoryPort ユーザーリポジトリのポート
 */
@Service
class UpdateProfileUseCase(
    private val profileRepositoryPort: ProfileRepositoryPort,
    private val userRepositoryPort: UserRepositoryPort
) {

    /**
     * プロフィールを更新する
     *
     * @param profile 更新するプロフィール
     * @return 更新されたプロフィール
     * @throws IllegalArgumentException バリデーションエラーの場合
     */
    fun updateProfile(profile: Profile): Profile {
        validateProfileData(profile)
        
        val existingProfile = profileRepositoryPort.findByUserId(profile.userId!!)
            ?: throw IllegalArgumentException("Profile for user ${profile.userId} not found")
        
        val updatedProfile = profile.copy(id = existingProfile.id)
        
        return profileRepositoryPort.save(updatedProfile)
    }

    /**
     * プロフィールデータの統合バリデーション
     *
     * @param profile バリデーション対象のプロフィール
     * @throws IllegalArgumentException バリデーションエラーの場合
     */
    private fun validateProfileData(profile: Profile) {
        val errors = mutableListOf<String>()

        if (profile.userId == null) {
            errors.add("User ID cannot be null")
        } else {
            val user = userRepositoryPort.findByUserId(profile.userId)
            if (user == null) {
                errors.add("User with id ${profile.userId} not found")
            }
        }

        if (profile.nickname.isBlank()) {
            errors.add("Nickname cannot be blank")
        }

        if (profile.nickname.length > 50) {
            errors.add("Nickname must be 50 characters or less")
        }

        if (profile.area.isBlank()) {
            errors.add("Area cannot be blank")
        }

        if (profile.occupation.isBlank()) {
            errors.add("Occupation cannot be blank")
        }

        if (errors.isNotEmpty()) {
            throw IllegalArgumentException("Validation failed: ${errors.joinToString(", ")}")
        }
    }
}