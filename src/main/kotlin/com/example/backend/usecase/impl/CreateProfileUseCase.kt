package com.example.backend.usecase.impl

import com.example.backend.domain.model.Profile
import com.example.backend.usecase.gateway.ProfileRepositoryPort
import com.example.backend.usecase.gateway.UserRepositoryPort
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.Period

/**
 * プロフィール作成ユースケース
 *
 * @property profileRepositoryPort プロフィールリポジトリのポート
 * @property userRepositoryPort ユーザーリポジトリのポート
 */
@Service
class CreateProfileUseCase(
    private val profileRepositoryPort: ProfileRepositoryPort,
    private val userRepositoryPort: UserRepositoryPort
) {

    /**
     * プロフィールを作成する
     *
     * @param profile 作成するプロフィール
     * @return 作成されたプロフィール
     * @throws IllegalArgumentException バリデーションエラーの場合
     */
    fun createProfile(profile: Profile): Profile {
        validateProfileData(profile)
        
        val existingProfile = profileRepositoryPort.findByUserId(profile.userId!!)
        if (existingProfile != null) {
            throw IllegalArgumentException("Profile for user ${profile.userId} already exists")
        }
        
        return profileRepositoryPort.save(profile)
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

        // 年齢制限のチェック（18歳以上、150歳以下）
        val age = Period.between(profile.birthdate, LocalDate.now()).years
        if (age < 18) {
            errors.add("18歳未満の方はご利用いただけません。")
        }
        if (age > 150) {
            errors.add("正しい生年月日を入力してください。")
        }

        if (errors.isNotEmpty()) {
            throw IllegalArgumentException("Validation failed: ${errors.joinToString(", ")}")
        }
    }
}
