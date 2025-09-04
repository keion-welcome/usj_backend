package com.example.backend.shared.util

import java.nio.ByteBuffer
import java.security.SecureRandom
import java.util.UUID

/**
 * UUID7生成ユーティリティクラス
 * 
 * RFC 9562準拠のUUID7（時系列順ソート可能なUUID）を生成します。
 * 
 * UUID7の特徴:
 * - タイムスタンプベースで時系列順にソート可能
 * - データベースのインデックス効率が良い
 * - 分散システムでの一意性を保証
 * - UUID4よりもパフォーマンスが優秀
 * 
 * @see <a href="https://tools.ietf.org/rfc/rfc9562.txt">RFC 9562</a>
 */
object Uuid7Utils {
    
    private val secureRandom = SecureRandom()
    
    /**
     * 現在のタイムスタンプでUUID7を生成
     * 
     * @return UUID7文字列（例: "01234567-89ab-7def-8123-456789abcdef"）
     */
    fun generate(): String {
        return generateAt(System.currentTimeMillis())
    }
    
    /**
     * 指定されたタイムスタンプでUUID7を生成
     * 
     * 主にテスト用途で使用。本番では generate() を使用することを推奨。
     * 
     * @param timestampMillis ミリ秒単位のタイムスタンプ
     * @return UUID7文字列
     */
    fun generateAt(timestampMillis: Long): String {
        val bytes = ByteArray(16)
        secureRandom.nextBytes(bytes)
        
        // タイムスタンプ（48ビット）を先頭6バイトに配置
        val timestampBuffer = ByteBuffer.allocate(8)
        timestampBuffer.putLong(timestampMillis)
        System.arraycopy(timestampBuffer.array(), 2, bytes, 0, 6)
        
        // バージョン7を設定（4ビット）
        bytes[6] = (bytes[6].toInt() and 0x0F or 0x70).toByte()
        
        // バリアント2を設定（2ビット）
        bytes[8] = (bytes[8].toInt() and 0x3F or 0x80).toByte()
        
        // 128ビットをUUIDに変換
        val buffer = ByteBuffer.wrap(bytes)
        val mostSignificantBits = buffer.long
        val leastSignificantBits = buffer.long
        
        return UUID(mostSignificantBits, leastSignificantBits).toString()
    }
} 