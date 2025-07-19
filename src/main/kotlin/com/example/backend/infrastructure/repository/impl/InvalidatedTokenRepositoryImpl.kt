package com.example.backend.infrastructure.repository.impl

import com.example.backend.domain.model.InvalidatedToken
import com.example.backend.infrastructure.entity.InvalidatedTokenEntity
import com.example.backend.infrastructure.repository.jpa.JpaInvalidatedTokenRepository
import com.example.backend.usecase.gateway.InvalidatedTokenRepositoryPort
import org.springframework.stereotype.Repository

/**
 * 無効化されたトークンリポジトリポートの実装クラス。
 *
 * @property jpaRepository JpaInvalidatedTokenRepository
 */
@Repository
class InvalidatedTokenRepositoryImpl(
    private val jpaRepository: JpaInvalidatedTokenRepository
) : InvalidatedTokenRepositoryPort {

    /**
     * トークンが無効化されているかどうかを確認します。
     *
     * @param token 確認するトークン
     * @return 無効化されていればtrue、そうでなければfalse
     */
    override fun exists(token: String): Boolean {
        return jpaRepository.existsById(token)
    }

    /**
     * トークンを無効化リストに保存します。
     *
     * @param invalidatedToken 無効化するトークン
     */
    override fun save(invalidatedToken: InvalidatedToken) {
        val entity = InvalidatedTokenEntity(
            token = invalidatedToken.token,
            expiryDate = invalidatedToken.expiryDate
        )
        jpaRepository.save(entity)
    }
}
