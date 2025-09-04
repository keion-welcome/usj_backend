package com.example.backend.infrastructure.repository.jdbc

import com.example.backend.domain.model.User
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class JdbcUserRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    /**
     * 複雑な検索条件でユーザーを検索
     */
    fun findByComplexCriteria(
        username: String? = null,
        email: String? = null,
        createdAfter: String? = null
    ): List<User> {
        val conditions = mutableListOf<String>()
        val params = mutableListOf<Any>()
        
        username?.let {
            conditions.add("username LIKE ?")
            params.add("%$it%")
        }
        
        email?.let {
            conditions.add("email LIKE ?")
            params.add("%$it%")
        }
        
        createdAfter?.let {
            conditions.add("created_at > ?")
            params.add(it)
        }
        
        val whereClause = if (conditions.isNotEmpty()) {
            "WHERE ${conditions.joinToString(" AND ")}"
        } else ""
        
        val sql = "SELECT id, user_id, username, email, password FROM users $whereClause ORDER BY created_at DESC"
        
        return jdbcTemplate.query(sql, params.toTypedArray()) { rs, _ ->
            User(
                id = rs.getLong("id"),
                userId = rs.getString("user_id"),
                username = rs.getString("username"),
                email = rs.getString("email"),
                password = rs.getString("password")
            )
        }
    }

    /**
     * ページングを使った検索
     */
    fun findWithPaging(offset: Int, limit: Int): List<User> {
        val sql = """
            SELECT id, user_id, username, email, password 
            FROM users 
            ORDER BY created_at DESC
            LIMIT ? OFFSET ?
        """.trimIndent()
        
        return jdbcTemplate.query(sql, arrayOf(limit, offset)) { rs, _ ->
            User(
                id = rs.getLong("id"),
                userId = rs.getString("user_id"),
                username = rs.getString("username"),
                email = rs.getString("email"),
                password = rs.getString("password")
            )
        }
    }

    /**
     * 複数テーブルをJOINした検索
     */
    fun findUsersWithProfiles(): List<Map<String, Any?>> {
        val sql = """
            SELECT u.id, u.user_id, u.username, u.email, 
                   p.display_name, p.bio, p.avatar_url
            FROM users u
            LEFT JOIN profiles p ON u.user_id = p.user_id
            ORDER BY u.created_at DESC
        """.trimIndent()
        
        return jdbcTemplate.queryForList(sql)
    }

    /**
     * バッチ処理でユーザーのステータスを更新
     */
    fun batchUpdateUserStatus(userIds: List<String>, status: String): IntArray {
        val sql = "UPDATE users SET status = ? WHERE user_id = ?"
        
        val batchArgs = userIds.map { arrayOf(status, it) }.toTypedArray()
        return jdbcTemplate.batchUpdate(sql, batchArgs)
    }

    /**
     * 高速検索（インデックス活用）
     */
    fun searchUsersByEmail(emailPattern: String): List<User> {
        val sql = """
            SELECT id, user_id, username, email, password 
            FROM users 
            WHERE email ILIKE ?
            ORDER BY email
        """.trimIndent()
        
        return jdbcTemplate.query(sql, arrayOf(emailPattern)) { rs, _ ->
            User(
                id = rs.getLong("id"),
                userId = rs.getString("user_id"),
                username = rs.getString("username"),
                email = rs.getString("email"),
                password = rs.getString("password")
            )
        }
    }

    /**
     * EXISTS句を使った検索
     */
    fun findUsersWithProfiles(hasProfile: Boolean): List<User> {
        val condition = if (hasProfile) "EXISTS" else "NOT EXISTS"
        
        val sql = """
            SELECT id, user_id, username, email, password 
            FROM users u
            WHERE $condition (
                SELECT 1 FROM profiles p WHERE p.user_id = u.user_id
            )
            ORDER BY u.created_at DESC
        """.trimIndent()
        
        return jdbcTemplate.query(sql) { rs, _ ->
            User(
                id = rs.getLong("id"),
                userId = rs.getString("user_id"),
                username = rs.getString("username"),
                email = rs.getString("email"),
                password = rs.getString("password")
            )
        }
    }
}