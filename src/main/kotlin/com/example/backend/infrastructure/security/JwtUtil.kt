package com.example.backend.infrastructure.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec
import kotlin.text.Charsets.UTF_8

/**
 * JWTトークンの生成・検証を行うユーティリティクラス（非推奨API非使用）
 */
@Component
class JwtUtil {

    // 🔐 シークレットキー（最低でも256bit必要。実運用では環境変数や設定ファイルから取得する）
    private val secret = "your-256-bit-secret-your-256-bit-secret" // 長さが足りないと例外になります

    // JWTの署名アルゴリズム（HMAC-SHA256）
    private val algorithm = SignatureAlgorithm.HS256

    // JWTの有効期限（10時間）
    private val expirationTimeMs: Long = 1000 * 60 * 60 * 10

    // 🔑 文字列キーをKey型に変換（非推奨な `setSigningKey(String)` を避ける）
    private val key: Key = SecretKeySpec(secret.toByteArray(UTF_8), algorithm.jcaName)

    /**
     * JWTトークンを生成する
     */
    fun generateToken(subject: String): String {
        val now = Date()
        val expiryDate = Date(now.time + expirationTimeMs)

        return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, algorithm) // ✅ 非推奨APIではなく `Key` を使う
            .compact()
    }

    /**
     * トークンの有効性を検証する
     */
    fun validateToken(token: String): Boolean {
        return try {
            val parser = Jwts.parserBuilder()
                .setSigningKey(key) // ✅ parserBuilder を使う
                .build()

            val claimsJws = parser.parseClaimsJws(token)
            !claimsJws.body.expiration.before(Date())
        } catch (ex: Exception) {
            false
        }
    }

    /**
     * トークンから subject（ユーザー識別子）を取得する
     */
    fun getSubject(token: String): String {
        val parser = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()

        return parser.parseClaimsJws(token).body.subject
    }
}
