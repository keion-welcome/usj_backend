package com.example.backend.infrastructure.repository.impl

import com.example.backend.domain.model.Profile
import com.example.backend.domain.model.Gender
import com.example.backend.usecase.gateway.ProfileRepositoryPort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.time.LocalDate

/**
 * プロフィールリポジトリポートのJDBC実装クラス
 *
 * @property jdbcTemplate JDBCテンプレート
 */
@Repository
class ProfileRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate
) : ProfileRepositoryPort {

    /**
     * プロフィールを保存する
     *
     * @param profile 保存するプロフィール
     * @return 保存されたプロフィール
     */
    override fun save(profile: Profile): Profile {
        val userId = profile.userId ?: throw IllegalArgumentException("User ID cannot be null")
        
        // ユーザーが存在するかチェック
        val userExists = (jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE id = ?",
            Int::class.java,
            userId
        ) ?: 0) > 0
        
        if (!userExists) {
            throw IllegalArgumentException("User entity not found")
        }
        
        return if (profile.id == null) {
            // 新規作成
            val keyHolder = GeneratedKeyHolder()
            jdbcTemplate.update({ connection ->
                val ps = connection.prepareStatement(
                    """
                    INSERT INTO profiles (user_id, nickname, gender, birthdate, area, occupation, has_annual_pass)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """.trimIndent(),
                    Statement.RETURN_GENERATED_KEYS
                )
                ps.setLong(1, userId)
                ps.setString(2, profile.nickname)
                ps.setString(3, profile.gender.name)
                ps.setDate(4, java.sql.Date.valueOf(profile.birthdate))
                ps.setString(5, profile.area)
                ps.setString(6, profile.occupation)
                ps.setBoolean(7, profile.hasAnnualPass)
                ps
            }, keyHolder)
            
            val generatedId = keyHolder.key?.toLong() ?: throw RuntimeException("Failed to get generated ID")
            profile.copy(id = generatedId)
        } else {
            // 更新
            jdbcTemplate.update(
                """
                UPDATE profiles 
                SET nickname = ?, gender = ?, birthdate = ?, area = ?, occupation = ?, has_annual_pass = ?
                WHERE id = ?
                """.trimIndent(),
                profile.nickname,
                profile.gender.name,
                java.sql.Date.valueOf(profile.birthdate),
                profile.area,
                profile.occupation,
                profile.hasAnnualPass,
                profile.id
            )
            profile
        }
    }

    /**
     * ユーザーIDでプロフィールを検索する
     *
     * @param userId ユーザーID
     * @return プロフィール（存在しない場合はnull）
     */
    override fun findByUserId(userId: Long?): Profile? {
        if (userId == null) return null
        
        return try {
            jdbcTemplate.queryForObject(
                """
                SELECT id, user_id, nickname, gender, birthdate, area, occupation, has_annual_pass
                FROM profiles
                WHERE user_id = ?
                """.trimIndent(),
                profileRowMapper,
                userId
            )
        } catch (e: Exception) {
            null
        }
    }

    private val profileRowMapper = RowMapper<Profile> { rs: ResultSet, _: Int ->
        Profile(
            id = rs.getLong("id"),
            userId = rs.getLong("user_id"),
            nickname = rs.getString("nickname"),
            gender = Gender.valueOf(rs.getString("gender")),
            birthdate = rs.getDate("birthdate").toLocalDate(),
            area = rs.getString("area"),
            occupation = rs.getString("occupation"),
            hasAnnualPass = rs.getBoolean("has_annual_pass")
        )
    }
}