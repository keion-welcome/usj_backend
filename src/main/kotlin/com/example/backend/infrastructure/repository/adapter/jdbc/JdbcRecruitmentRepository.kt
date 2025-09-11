package com.example.backend.infrastructure.repository.adapter.jdbc

import com.example.backend.domain.model.Recruitment
import com.example.backend.domain.model.RecruitmentStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class JdbcRecruitmentRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    /**
     * 募集を作成
     */
    fun createRecruitment(recruitment: Recruitment): Recruitment {
        val sql = """
            INSERT INTO recruitments (user_id, title, description, max_participants, 
                                    status, attraction_name, expires_at, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
        """.trimIndent()
        
        jdbcTemplate.update(
            sql, 
            recruitment.userId,
            recruitment.title,
            recruitment.description,
            recruitment.maxParticipants,
            recruitment.status.name,
            recruitment.attractionName,
            recruitment.expiresAt
        )
        return recruitment
    }

    /**
     * 募集情報の更新
     */
    fun updateRecruitment(recruitment: Recruitment): Recruitment {
        val sql = """
            UPDATE recruitments 
            SET title = ?, description = ?, max_participants = ?,
                status = ?, attraction_name = ?, expires_at = ?, updated_at = NOW()
            WHERE id = ?
        """.trimIndent()
        
        jdbcTemplate.update(
            sql,
            recruitment.title,
            recruitment.description,
            recruitment.maxParticipants,
            recruitment.status.name,
            recruitment.attractionName,
            recruitment.expiresAt,
            recruitment.id
        )
        return recruitment
    }

    /**
     * 募集の削除
     */
    fun deleteRecruitment(id: Long): Boolean {
        val sql = "DELETE FROM recruitments WHERE id = ?"
        val rowsAffected = jdbcTemplate.update(sql, id)
        return rowsAffected > 0
    }

    /**
     * IDで募集を検索
     */
    fun findById(id: Long): Recruitment? {
        val sql = """
            SELECT id, user_id, title, description, max_participants,
                   status, attraction_name, expires_at
            FROM recruitments 
            WHERE id = ?
        """.trimIndent()
        
        return try {
            jdbcTemplate.queryForObject(sql, arrayOf(id)) { rs, _ ->
                Recruitment(
                    id = rs.getLong("id"),
                    userId = rs.getLong("user_id"),
                    title = rs.getString("title"),
                    description = rs.getString("description"),
                    maxParticipants = rs.getInt("max_participants"),
                    status = RecruitmentStatus.valueOf(rs.getString("status")),
                    attractionName = rs.getString("attraction_name"),
                    expiresAt = rs.getTimestamp("expires_at")?.toLocalDateTime()
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * ユーザーIDで募集を検索
     */
    fun findByUserId(userId: Long): List<Recruitment> {
        val sql = """
            SELECT id, user_id, title, description, max_participants,
                   status, attraction_name, expires_at
            FROM recruitments 
            WHERE user_id = ?
            ORDER BY created_at DESC
        """.trimIndent()
        
        return jdbcTemplate.query(sql, arrayOf(userId)) { rs, _ ->
            Recruitment(
                id = rs.getLong("id"),
                userId = rs.getLong("user_id"),
                title = rs.getString("title"),
                description = rs.getString("description"),
                maxParticipants = rs.getInt("max_participants"),
                status = RecruitmentStatus.valueOf(rs.getString("status")),
                attractionName = rs.getString("attraction_name"),
                expiresAt = rs.getTimestamp("expires_at")?.toLocalDateTime()
            )
        }
    }

    /**
     * ステータスで募集を検索
     */
    fun findByStatus(status: RecruitmentStatus): List<Recruitment> {
        val sql = """
            SELECT id, user_id, title, description, max_participants,
                   status, attraction_name, expires_at
            FROM recruitments 
            WHERE status = ?
            ORDER BY created_at DESC
        """.trimIndent()
        
        return jdbcTemplate.query(sql, arrayOf(status.name)) { rs, _ ->
            Recruitment(
                id = rs.getLong("id"),
                userId = rs.getLong("user_id"),
                title = rs.getString("title"),
                description = rs.getString("description"),
                maxParticipants = rs.getInt("max_participants"),
                status = RecruitmentStatus.valueOf(rs.getString("status")),
                attractionName = rs.getString("attraction_name"),
                expiresAt = rs.getTimestamp("expires_at")?.toLocalDateTime()
            )
        }
    }

    /**
     * アトラクション名で募集を検索
     */
    fun findByAttractionName(attractionName: String): List<Recruitment> {
        val sql = """
            SELECT id, user_id, title, description, max_participants,
                   status, attraction_name, expires_at
            FROM recruitments 
            WHERE attraction_name = ?
            ORDER BY created_at DESC
        """.trimIndent()
        
        return jdbcTemplate.query(sql, arrayOf(attractionName)) { rs, _ ->
            Recruitment(
                id = rs.getLong("id"),
                userId = rs.getLong("user_id"),
                title = rs.getString("title"),
                description = rs.getString("description"),
                maxParticipants = rs.getInt("max_participants"),
                status = RecruitmentStatus.valueOf(rs.getString("status")),
                attractionName = rs.getString("attraction_name"),
                expiresAt = rs.getTimestamp("expires_at")?.toLocalDateTime()
            )
        }
    }

    /**
     * 全募集を取得
     */
    fun findAll(): List<Recruitment> {
        val sql = """
            SELECT id, user_id, title, description, max_participants,
                   status, attraction_name, expires_at
            FROM recruitments 
            ORDER BY created_at DESC
        """.trimIndent()
        
        return jdbcTemplate.query(sql) { rs, _ ->
            Recruitment(
                id = rs.getLong("id"),
                userId = rs.getLong("user_id"),
                title = rs.getString("title"),
                description = rs.getString("description"),
                maxParticipants = rs.getInt("max_participants"),
                status = RecruitmentStatus.valueOf(rs.getString("status")),
                attractionName = rs.getString("attraction_name"),
                expiresAt = rs.getTimestamp("expires_at")?.toLocalDateTime()
            )
        }
    }

    /**
     * 複雑な検索条件で募集を検索
     */
    fun findByComplexCriteria(
        title: String? = null,
        attractionName: String? = null,
        status: RecruitmentStatus? = null,
        maxParticipants: Int? = null
    ): List<Recruitment> {
        val conditions = mutableListOf<String>()
        val params = mutableListOf<Any>()
        
        title?.let {
            conditions.add("title ILIKE ?")
            params.add("%$it%")
        }
        
        attractionName?.let {
            conditions.add("attraction_name = ?")
            params.add(it)
        }
        
        status?.let {
            conditions.add("status = ?")
            params.add(it.name)
        }
        
        maxParticipants?.let {
            conditions.add("max_participants >= ?")
            params.add(it)
        }
        
        val whereClause = if (conditions.isNotEmpty()) {
            "WHERE ${conditions.joinToString(" AND ")}"
        } else ""
        
        val sql = """
            SELECT id, user_id, title, description, max_participants,
                   status, attraction_name, expires_at
            FROM recruitments $whereClause 
            ORDER BY created_at DESC
        """.trimIndent()
        
        return jdbcTemplate.query(sql, params.toTypedArray()) { rs, _ ->
            Recruitment(
                id = rs.getLong("id"),
                userId = rs.getLong("user_id"),
                title = rs.getString("title"),
                description = rs.getString("description"),
                maxParticipants = rs.getInt("max_participants"),
                status = RecruitmentStatus.valueOf(rs.getString("status")),
                attractionName = rs.getString("attraction_name"),
                expiresAt = rs.getTimestamp("expires_at")?.toLocalDateTime()
            )
        }
    }
}