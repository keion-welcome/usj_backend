package com.example.backend.usecase.usecase

import com.example.backend.domain.model.Profile
import com.example.backend.usecase.gateway.ProfileRepositoryPort
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

/**
 * プロフィール更新ユースケース
 *
 * @property profileRepositoryPort プロフィールリポジトリのポート
 */
@Service
class UpdateProfileUseCase(private val profileRepositoryPort: ProfileRepositoryPort) {

    /**
     * プロフィールを更新する
     *
     * @param profileId 更新するプロフィールのID
     * @param updatedProfile 更新後のプロフィール情報
     * @param currentUserId 認証済みユーザーのID
     * @return 更新されたプロフィール
     * @throws AccessDeniedException 権限がない場合
     */
    fun updateProfile(profileId: Long, updatedProfile: Profile, currentUserId: Long): Profile {
        val existingProfile = profileRepositoryPort.findById(profileId)
            ?: throw NoSuchElementException("Profile not found with id: $profileId")

        if (existingProfile.userId != currentUserId) {
            throw AccessDeniedException("You are not authorized to edit this profile.")
        }

        val newProfile = existingProfile.copy(
            nickname = updatedProfile.nickname,
            gender = updatedProfile.gender,
            birthdate = updatedProfile.birthdate,
            area = updatedProfile.area,
            occupation = updatedProfile.occupation,
            hasAnnualPass = updatedProfile.hasAnnualPass
        )

        return profileRepositoryPort.save(newProfile)
    }
}
