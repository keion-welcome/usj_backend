package com.example.backend.infrastructure.repository.jpa

import com.example.backend.infrastructure.entity.InvalidatedTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

/**
 * 無効化されたトークンエンティティのためのJPAリポジトリ。
 */
interface JpaInvalidatedTokenRepository : JpaRepository<InvalidatedTokenEntity, String> {
    /**
     * 指定された有効期限より前のトークンをすべて削除します。
     *
     * @param now 現在時刻
     */
    fun deleteByExpiryDateBefore(now: Instant)
}
