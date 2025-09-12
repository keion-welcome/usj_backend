package com.example.backend.usecase.gateway

import com.example.backend.domain.model.Attraction

/**
 * アトラクションリポジトリポート
 */
interface AttractionRepositoryPort {
    /**
     * すべてのアトラクションを取得する
     *
     * @return アトラクションのリスト
     */
    fun findAll(): List<Attraction>

    /**
     * アクティブなアトラクションのみを取得する
     *
     * @return アクティブなアトラクションのリスト
     */
    fun findActiveAttractions(): List<Attraction>

    /**
     * アトラクションをIDで取得する
     *
     * @param id アトラクションID
     * @return アトラクション、存在しない場合はnull
     */
    fun findById(id: Long): Attraction?

    /**
     * アトラクション名で検索する
     *
     * @param name アトラクション名
     * @return 該当するアトラクションのリスト
     */
    fun findByNameContaining(name: String): List<Attraction>

    /**
     * アトラクションの待ち時間を更新する
     *
     * @param id アトラクションID
     * @param waitTime 新しい待ち時間
     * @return 更新されたアトラクション
     */
    fun updateWaitTime(id: Long, waitTime: Int): Attraction?
}