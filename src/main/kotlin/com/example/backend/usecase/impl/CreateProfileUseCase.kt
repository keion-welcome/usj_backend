package com.example.backend.usecase.impl

import com.example.backend.domain.model.Profile
import com.example.backend.usecase.gateway.ProfileRepositoryPort
import com.example.backend.usecase.gateway.UserRepositoryPort
import org.springframework.stereotype.Service

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
        // ビジネスロジック：ユーザー存在確認
        userRepositoryPort.findById(profile.userId)
            ?: throw IllegalArgumentException("User with id ${profile.userId} not found")
        
        // ビジネスロジック：プロフィール重複確認
        val existingProfile = profileRepositoryPort.findByUserId(profile.userId)
        if (existingProfile != null) {
            throw IllegalArgumentException("Profile for user ${profile.userId} already exists")
        }
        
        // データアクセス処理
        return profileRepositoryPort.save(profile)
    }
}
