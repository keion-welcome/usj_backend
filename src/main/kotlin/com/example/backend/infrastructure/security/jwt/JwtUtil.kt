package com.example.backend.infrastructure.security.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.security.Key
import java.util.Date
import javax.crypto.spec.SecretKeySpec

/**
 * JWTトークンの生成・検証を行うユーティリティクラス
 */
@Component
class JwtUtil {

    // 🔐 シークレットキー（最低でも256bit必要。実運用では環境変数や設定ファイルから取得する）
    private val secret = "your-256-bit-secret-your-256-bit-secret" // 長さが足りないと例外になります

    // JWTの署名アルゴリズム（HMAC-SHA256）
    private val algorithm = SignatureAlgorithm.HS256

    // JWTの有効期限（16時間）
    private val expirationTimeMs: Long = 1000 * 60 * 60 * 16

    // 🔑 文字列キーをKey型に変換
    private val key: Key = SecretKeySpec(secret.toByteArray(Charsets.UTF_8), algorithm.jcaName)

    /**
     * JWTトークンを生成する
     * @param email ユーザーのメールアドレス
     * @return 生成されたJWTトークン
     */
    fun generateToken(email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + expirationTimeMs)

        return Jwts.builder()
            .setSubject(email)  // subjectとしてメールアドレスを使用
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, algorithm)
            .compact()
    }

    /**
     * トークンの有効性を検証する
     * @param token JWTトークン
     * @param emailFromDb DBから取得したユーザーのメールアドレス
     * @return 有効な場合はtrue
     */
    fun validateToken(token: String, emailFromDb: String): Boolean {
        return try {
            val emailFromToken = getSubject(token)
            // トークンのsubjectとDBのユーザーメールアドレスが一致し、かつ期限切れでないことを確認
            emailFromToken == emailFromDb && !isTokenExpired(token)
        } catch (ex: Exception) {
            false
        }
    }

    /**
     * トークンから subject（ユーザー識別子）を取得する
     * @param token JWTトークン
     * @return ユーザー識別子（このアプリケーションではメールアドレス）
     */
    fun getSubject(token: String): String {
        val parser = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()

        return parser.parseClaimsJws(token).body.subject
    }

    /**
     * トークンが期限切れかどうかをチェックする
     * @param token JWTトークン
     * @return 期限切れの場合はtrue
     */
    private fun isTokenExpired(token: String): Boolean {
        val parser = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
        return parser.parseClaimsJws(token).body.expiration.before(Date())
    }

    /**
     * トークンから有効期限を取得する
     * @param token JWTトークン
     * @return 有効期限
     */
    fun getExpirationDate(token: String): Date {
        val parser = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
        return parser.parseClaimsJws(token).body.expiration
    }
}