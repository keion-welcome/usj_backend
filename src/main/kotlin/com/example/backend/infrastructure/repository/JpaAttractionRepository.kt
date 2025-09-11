package com.example.backend.infrastructure.repository

import com.example.backend.infrastructure.entity.AttractionEntity
import org.springframework.data.jpa.repository.JpaRepository

/**
 * アトラクションリポジトリインターフェース
 */
interface JpaAttractionRepository : JpaRepository<AttractionEntity, Long> {
    
    /**
     * アクティブなアトラクションを取得
     *
     * @param isActive アクティブフラグ
     * @return アトラクションリスト
     */
    fun findByIsActiveOrderByNameAsc(isActive: Boolean): List<AttractionEntity>

    /**
     * アトラクション名で部分一致検索
     *
     * @param name アトラクション名
     * @return アトラクションリスト
     */
    fun findByNameContainingIgnoreCase(name: String): List<AttractionEntity>

    /**
     * アクティブなアトラクションから名前で部分一致検索
     *
     * @param name アトラクション名
     * @param isActive アクティブフラグ
     * @return アトラクションリスト
     */
    fun findByNameContainingIgnoreCaseAndIsActive(name: String, isActive: Boolean): List<AttractionEntity>
}