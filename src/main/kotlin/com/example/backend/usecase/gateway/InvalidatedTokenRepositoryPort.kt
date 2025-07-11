package com.example.backend.usecase.gateway

import com.example.backend.domain.model.InvalidatedToken

/**
 * 無効化されたトークンリポジトリへのポート（インターフェース）。
 * ユースケース層はこのインターフェースに依存します。
 */
interface InvalidatedTokenRepositoryPort {
    /**
     * トークンが無効化されているかどうかを確認します。
     *
     * @param token 確認するトークン
     * @return 無効化されていればtrue、そうでなければfalse
     */
    fun exists(token: String): Boolean

    /**
     * トークンを無効化リストに保存します。
     *
     * @param invalidatedToken 無効化するトークン
     */
    fun save(invalidatedToken: InvalidatedToken)
}
