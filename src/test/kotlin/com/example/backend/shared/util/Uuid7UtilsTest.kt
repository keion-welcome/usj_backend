package com.example.backend.shared.util

import com.example.backend.shared.util.Uuid7Utils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.RepeatedTest
import java.util.UUID
import java.time.Instant

/**
 * Uuid7Utilsのテスト
 * 
 * ランダム生成のため値の完全な予測は不可能だが、
 * 以下の要素をテストする：
 * - UUIDフォーマットの妥当性
 * - バージョン7であること
 * - 時系列順序性
 * - 一意性
 * - 指定タイムスタンプでの生成
 */
@DisplayName("UUID7ユーティリティ")
class Uuid7UtilsTest {

    @Test
    @DisplayName("有効なUUIDフォーマットで生成される")
    fun `should generate valid UUID format`() {
        // When
        val uuid7 = Uuid7Utils.generate()
        
        // Then
        assertThat(uuid7).matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
        
        // UUID.fromStringで例外が発生しないことも確認
        org.assertj.core.api.Assertions.assertThatCode { UUID.fromString(uuid7) }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("バージョン7のUUIDが生成される")
    fun `should generate UUID version 7`() {
        // When
        val uuid7 = Uuid7Utils.generate()
        val uuid = UUID.fromString(uuid7)
        
        // Then
        assertThat(uuid.version()).isEqualTo(7)
    }

    @RepeatedTest(10)
    @DisplayName("複数回実行しても時系列順序性が保たれる")
    fun `should generate time-ordered UUIDs`() {
        // When
        val uuid1 = Uuid7Utils.generate()
        Thread.sleep(2) // 最低2ミリ秒の間隔を保証
        val uuid2 = Uuid7Utils.generate()
        
        // Then - 文字列としてソートしても時系列順になる
        assertThat(uuid1).isLessThan(uuid2)
        
        // リストでソートしても順序が保たれる
        val uuids = listOf(uuid2, uuid1)
        val sorted = uuids.sorted()
        assertThat(sorted).containsExactly(uuid1, uuid2)
    }

    @Test
    @DisplayName("大量生成時の一意性が保たれる")
    fun `should generate unique UUIDs in bulk`() {
        // When - 1000個のUUID7を生成
        val uuids = (1..1000).map { Uuid7Utils.generate() }.toSet()
        
        // Then - 全て異なる値であること
        assertThat(uuids).hasSize(1000)
    }

    @Test
    @DisplayName("指定されたタイムスタンプでUUID7が生成される")
    fun `should generate UUID7 with specified timestamp`() {
        // Given
        val specificTime = Instant.parse("2024-01-01T00:00:00Z").toEpochMilli()
        
        // When
        val uuid7 = Uuid7Utils.generateAt(specificTime)
        val uuid = UUID.fromString(uuid7)
        
        // Then
        assertThat(uuid.version()).isEqualTo(7)
        
        // タイムスタンプが含まれていることを間接的に確認
        // （UUID7の先頭48ビットにタイムスタンプが埋め込まれている）
        val timestampPart = uuid.mostSignificantBits ushr 16
        assertThat(timestampPart).isEqualTo(specificTime)
    }

    @Test
    @DisplayName("異なるタイムスタンプで生成したUUID7は時系列順になる")
    fun `should respect timestamp order in generateAt`() {
        // Given
        val time1 = 1704067200000L // 2024-01-01 00:00:00
        val time2 = 1704153600000L // 2024-01-02 00:00:00
        
        // When
        val uuid1 = Uuid7Utils.generateAt(time1)
        val uuid2 = Uuid7Utils.generateAt(time2)
        
        // Then
        assertThat(uuid1).isLessThan(uuid2)
    }

    @Test
    @DisplayName("現在時刻生成と指定時刻生成の整合性")
    fun `should be consistent between current time and specified time generation`() {
        // Given
        val currentTime = System.currentTimeMillis()
        
        // When
        val uuid1 = Uuid7Utils.generate()
        val uuid2 = Uuid7Utils.generateAt(currentTime)
        
        // Then - 両方ともバージョン7であること
        assertThat(UUID.fromString(uuid1).version()).isEqualTo(7)
        assertThat(UUID.fromString(uuid2).version()).isEqualTo(7)
        
        // タイムスタンプ部分が近い値であること（完全一致は期待しない）
        val timestamp1 = UUID.fromString(uuid1).mostSignificantBits ushr 16
        val timestamp2 = UUID.fromString(uuid2).mostSignificantBits ushr 16
        val timeDiff = Math.abs(timestamp1 - timestamp2)
        assertThat(timeDiff).isLessThan(100L) // 100ミリ秒以内の差
    }

    @Test
    @DisplayName("連続生成時のパフォーマンス確認")
    fun `should generate UUIDs efficiently`() {
        // When - 10000個を連続生成
        val startTime = System.currentTimeMillis()
        val uuids = (1..10000).map { Uuid7Utils.generate() }
        val endTime = System.currentTimeMillis()
        
        // Then
        assertThat(uuids).hasSize(10000)
        assertThat(uuids.toSet()).hasSize(10000) // 全て異なること
        
        val duration = endTime - startTime
        assertThat(duration).isLessThan(1000L) // 1秒以内で完了すること
    }
} 