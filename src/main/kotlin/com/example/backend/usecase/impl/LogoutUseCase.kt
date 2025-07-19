package com.example.backend.usecase.impl

import com.example.backend.domain.model.InvalidatedToken
import com.example.backend.usecase.gateway.InvalidatedTokenRepositoryPort
import com.example.backend.infrastructure.security.jwt.JwtUtil
import org.springframework.stereotype.Service

/**
 * ログアウト処理のユースケース。
 *
 * @property invalidatedTokenRepositoryPort 無効化されたトークンリポジトリ
 * @property jwtUtil JWTユーティリティ
 */
@Service
class LogoutUseCase(
    private val invalidatedTokenRepositoryPort: InvalidatedTokenRepositoryPort,
    private val jwtUtil: JwtUtil
) {
    /**
     * 指定されたトークンを無効化します。
     *
     * @param token 無効化するJWTトークン
     */
    fun execute(token: String) {
        val jwt = token.substring(7)
        val expiryDate = jwtUtil.getExpirationDate(jwt)
        val invalidatedToken = InvalidatedToken(
            token = jwt,
            expiryDate = expiryDate.toInstant()
        )
        invalidatedTokenRepositoryPort.save(invalidatedToken)
    }
}
