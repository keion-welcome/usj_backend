package com.example.backend.usecase.gateway

import com.example.backend.domain.model.Profile

/**
 * プロフィールリポジトリのポートインターフェース
 */
interface ProfileRepositoryPort {
    /**
     * プロフィールを保存する
     *
     * @param profile 保存するプロフィール
     * @return 保存されたプロフィール
     */
    fun save(profile: Profile): Profile
}
