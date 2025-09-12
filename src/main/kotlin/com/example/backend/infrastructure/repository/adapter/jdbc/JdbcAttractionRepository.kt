package com.example.backend.infrastructure.repository.adapter.jdbc

import com.example.backend.domain.model.Attraction
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class JdbcAttractionRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    /**
     * アトラクションを作成
     */
    fun createAttraction(attraction: Attraction): Attraction {
        val sql = """
            INSERT INTO attractions (name, description, is_active, created_at, updated_at)
            VALUES (?, ?, ?, NOW(), NOW())
        """.trimIndent()
        
        jdbcTemplate.update(sql, attraction.name, attraction.description, attraction.isActive)
        return attraction
    }

    /**
     * アトラクション情報の更新
     */
    fun updateAttraction(attraction: Attraction): Attraction {
        val sql = """
            UPDATE attractions 
            SET name = ?, description = ?, is_active = ?, updated_at = NOW()
            WHERE id = ?
        """.trimIndent()
        
        jdbcTemplate.update(sql, attraction.name, attraction.description, attraction.isActive, attraction.id)
        return attraction
    }

    /**
     * アトラクションの削除
     */
    fun deleteAttraction(id: Long): Boolean {
        val sql = "DELETE FROM attractions WHERE id = ?"
        val rowsAffected = jdbcTemplate.update(sql, id)
        return rowsAffected > 0
    }

    /**
     * IDでアトラクションを検索
     */
    fun findById(id: Long): Attraction? {
        val sql = """
            SELECT id, name, description, is_active 
            FROM attractions 
            WHERE id = ?
        """.trimIndent()
        
        return try {
            jdbcTemplate.queryForObject(sql, arrayOf(id)) { rs, _ ->
                Attraction(
                    id = rs.getLong("id"),
                    name = rs.getString("name"),
                    description = rs.getString("description"),
                    isActive = rs.getBoolean("is_active")
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 名前でアトラクションを検索
     */
    fun findByName(name: String): Attraction? {
        val sql = """
            SELECT id, name, description, is_active 
            FROM attractions 
            WHERE name = ?
        """.trimIndent()
        
        return try {
            jdbcTemplate.queryForObject(sql, arrayOf(name)) { rs, _ ->
                Attraction(
                    id = rs.getLong("id"),
                    name = rs.getString("name"),
                    description = rs.getString("description"),
                    isActive = rs.getBoolean("is_active")
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 全アトラクションを取得
     */
    fun findAll(): List<Attraction> {
        val sql = """
            SELECT id, name, description, is_active 
            FROM attractions 
            ORDER BY name ASC
        """.trimIndent()
        
        return jdbcTemplate.query(sql) { rs, _ ->
            Attraction(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                description = rs.getString("description"),
                isActive = rs.getBoolean("is_active")
            )
        }
    }

    /**
     * アクティブなアトラクションのみ取得
     */
    fun findActiveAttractions(): List<Attraction> {
        val sql = """
            SELECT id, name, description, is_active 
            FROM attractions 
            WHERE is_active = true
            ORDER BY name ASC
        """.trimIndent()
        
        return jdbcTemplate.query(sql) { rs, _ ->
            Attraction(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                description = rs.getString("description"),
                isActive = rs.getBoolean("is_active")
            )
        }
    }

    /**
     * 名前の部分一致検索
     */
    fun searchByName(namePattern: String): List<Attraction> {
        val sql = """
            SELECT id, name, description, is_active 
            FROM attractions 
            WHERE name ILIKE ?
            ORDER BY name ASC
        """.trimIndent()
        
        return jdbcTemplate.query(sql, arrayOf("%$namePattern%")) { rs, _ ->
            Attraction(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                description = rs.getString("description"),
                isActive = rs.getBoolean("is_active")
            )
        }
    }
}