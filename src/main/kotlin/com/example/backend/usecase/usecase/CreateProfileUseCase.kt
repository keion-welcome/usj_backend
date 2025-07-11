package com.example.backend.usecase.usecase

import com.example.backend.domain.model.Profile
import com.example.backend.usecase.gateway.ProfileRepositoryPort
import org.springframework.stereotype.Service

/**
 * プロフィール作成ユースケース
 *
 * @property profileRepositoryPort プロフィールリポジトリのポート
 */
@Service
class CreateProfileUseCase(private val profileRepositoryPort: ProfileRepositoryPort) {

    /**
     * プロフィールを作成する
     *
     * @param profile 作成するプロフィール
     * @return 作成されたプロフィール
     */
    fun createProfile(profile: Profile): Profile {
        // ここにビジネスロジックを実装する
        // 例えば、バリデーションなど
        return profileRepositoryPort.save(profile)
    }
}
