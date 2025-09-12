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
     * @throws IllegalArgumentException ユーザーが存在しない場合
     */
    fun createProfile(profile: Profile): Profile {
        // userIdがnullの場合はエラー
        val userId = profile.userId ?: throw IllegalArgumentException("User ID cannot be null")
        
        // ビジネスルール：ユーザー存在確認
        userRepositoryPort.findById(userId)
            ?: throw IllegalArgumentException("User with id $userId not found")
        
        // ビジネスルール：プロフィール重複確認
        val existingProfile = profileRepositoryPort.findByUserId(userId)
        if (existingProfile != null) {
            throw IllegalArgumentException("Profile for user $userId already exists")
        }
        
        // ビジネスルール：年齢制限チェック（18歳以上）
        validateAge(profile.birthdate)
        
        // データアクセス処理
        return profileRepositoryPort.save(profile)
    }
    
    /**
     * 年齢制限のチェック（18歳以上、150歳以下）
     */
    private fun validateAge(birthdate: LocalDate) {
        val age = Period.between(birthdate, LocalDate.now()).years
        
        // 18歳未満は利用不可
        require(age >= 18) {
            "18歳未満の方はご利用いただけません。"
        }
        
        // 150歳以上は不正な入力とみなす
        require(age <= 150) {
            "正しい生年月日を入力してください。"
        }
    }
    
}
