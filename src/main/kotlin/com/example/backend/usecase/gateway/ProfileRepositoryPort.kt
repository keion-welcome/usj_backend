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

    /**
     * ユーザーIDでプロフィールを検索する
     *
     * @param userId ユーザーID
     * @return 見つかったプロフィール、なければnull
     */
    fun findByUserId(userId: Long): Profile?

    /**
     * プロフィールIDでプロフィールを検索する
     *
     * @param id プロフィールID
     * @return 見つかったプロフィール、なければnull
     */
    fun findById(id: Long): Profile?
}
