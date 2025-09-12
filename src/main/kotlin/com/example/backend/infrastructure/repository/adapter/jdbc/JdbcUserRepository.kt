package com.example.backend.infrastructure.repository.adapter.jdbc

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
        
        val sql = "SELECT id, email, password FROM users $whereClause ORDER BY created_at DESC"
        
        return jdbcTemplate.query(sql, params.toTypedArray()) { rs, _ ->
            User(
                id = null,
                userId = rs.getString("id"),
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
            SELECT id, email, password 
            FROM users 
            ORDER BY created_at DESC
            LIMIT ? OFFSET ?
        """.trimIndent()
        
        return jdbcTemplate.query(sql, arrayOf(limit, offset)) { rs, _ ->
            User(
                id = null,
                userId = rs.getString("id"),
                email = rs.getString("email"),
                password = rs.getString("password")
            )
        }
    }

    /**
     * 基本的なユーザー作成
     */
    fun createUser(user: User): User {
        val sql = """
            INSERT INTO users (id, email, password, created_at, updated_at)
            VALUES (?, ?, ?, NOW(), NOW())
        """.trimIndent()
        
        jdbcTemplate.update(sql, user.userId, user.email, user.password)
        return user
    }

    /**
     * ユーザー情報の更新
     */
    fun updateUser(user: User): User {
        val sql = """
            UPDATE users 
            SET email = ?, updated_at = NOW()
            WHERE id = ?
        """.trimIndent()
        
        jdbcTemplate.update(sql, user.email, user.userId)
        return user
    }

    /**
     * ユーザーの削除
     */
    fun deleteUser(userId: String): Boolean {
        val sql = "DELETE FROM users WHERE id = ?"
        val rowsAffected = jdbcTemplate.update(sql, userId)
        return rowsAffected > 0
    }

    /**
     * ユーザーIDでユーザーを検索
     */
    fun findByUserId(userId: String): User? {
        val sql = """
            SELECT id, email, password 
            FROM users 
            WHERE id = ?
        """.trimIndent()
        
        return try {
            jdbcTemplate.queryForObject(sql, arrayOf(userId)) { rs, _ ->
                User(
                    id = null,
                    userId = rs.getString("id"),
                    email = rs.getString("email"),
                    password = rs.getString("password")
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * メールアドレスでユーザーを検索
     */
    fun findByEmail(email: String): User? {
        val sql = """
            SELECT id, email, password 
            FROM users 
            WHERE email = ?
        """.trimIndent()
        
        return try {
            jdbcTemplate.queryForObject(sql, arrayOf(email)) { rs, _ ->
                User(
                    id = null,
                    userId = rs.getString("id"),
                    email = rs.getString("email"),
                    password = rs.getString("password")
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 高速検索（インデックス活用）
     */
    fun searchUsersByEmail(emailPattern: String): List<User> {
        val sql = """
            SELECT id, email, password 
            FROM users 
            WHERE email ILIKE ?
            ORDER BY email
        """.trimIndent()
        
        return jdbcTemplate.query(sql, arrayOf(emailPattern)) { rs, _ ->
            User(
                id = null,
                userId = rs.getString("id"),
                email = rs.getString("email"),
                password = rs.getString("password")
            )
        }
    }

    /**
     * 全ユーザーを取得
     */
    fun findAll(): List<User> {
        val sql = """
            SELECT id, email, password 
            FROM users 
            ORDER BY created_at DESC
        """.trimIndent()
        
        return jdbcTemplate.query(sql) { rs, _ ->
            User(
                id = null,
                userId = rs.getString("id"),
                email = rs.getString("email"),
                password = rs.getString("password")
            )
        }
    }
}