package com.example.backend.infrastructure.repository.impl

import com.example.backend.domain.model.User
import com.example.backend.infrastructure.entity.UserEntity
import com.example.backend.infrastructure.repository.jpa.JpaUserRepository
import com.example.backend.infrastructure.repository.jdbc.JdbcUserRepository
import com.example.backend.usecase.gateway.UserRepositoryPort
import com.example.backend.shared.util.Uuid7Utils
import org.springframework.stereotype.Repository

/**
 * JPAとJDBCを使い分けてユーザーをDBに保存・検索する実装クラス。
 * UserRepositoryPortを実装する。
 */
@Repository
class UserRepositoryImpl(
    private val jpaRepository: JpaUserRepository, // JPAリポジトリ（単純なCRUD）
    private val jdbcRepository: JdbcUserRepository // JDBCリポジトリ（複雑なクエリ）
) : UserRepositoryPort {

    /**
     * ユーザーを保存（新規登録）
     */
    override fun save(user: User): User {
        val entity = UserEntity(
            id = user.id ?: throw IllegalArgumentException("User.id must be provided (generated in use case)"),
            email = user.email,
            password = user.password
        )
        val saved = jpaRepository.save(entity)
        
        // ID生成の検証
        requireNotNull(saved.id) { 
            "Critical error: UserEntity.id generation failed for user: ${saved.email}" 
        }
        require(saved.id!!.isNotBlank()) { 
            "Critical error: UserEntity.id is blank for user: ${saved.email}" 
        }
        
        return User(saved.id, saved.email, saved.password)
    }

    /**
     * メールアドレスでユーザーを検索
     */
    override fun findByEmail(email: String): User? {
        return jpaRepository.findByEmail(email)?.let {
            User(it.id, it.email, it.password)
        }
    }

    /**
     * IDでユーザーを検索
     */
    override fun findById(id: String): User? {
        return jpaRepository.findById(id).orElse(null)?.let {
            User(it.id, it.email, it.password)
        }
    }
    
    // === JDBC使用の複雑な検索メソッド ===
    
    /**
     * 複雑な条件でユーザーを検索（JDBC使用）
     */
    fun searchUsers(
        username: String? = null,
        email: String? = null,
        createdAfter: String? = null
    ): List<User> {
        return jdbcRepository.findByComplexCriteria(username, email, createdAfter)
    }
    
    /**
     * ページング検索（JDBC使用）
     */
    fun getUsersWithPaging(page: Int, size: Int): List<User> {
        val offset = page * size
        return jdbcRepository.findWithPaging(offset, size)
    }
    
    /**
     * プロフィール情報付きユーザー検索（JDBC使用）
     */
    fun getUsersWithProfileInfo(): List<Map<String, Any?>> {
        return jdbcRepository.findUsersWithProfiles()
    }
    
    /**
     * 高速メール検索（JDBC使用）
     */
    fun fastEmailSearch(emailPattern: String): List<User> {
        return jdbcRepository.searchUsersByEmail(emailPattern)
    }
    
    /**
     * プロフィール有無での検索（JDBC使用）
     */
    fun findUsersWithProfileStatus(hasProfile: Boolean): List<User> {
        return jdbcRepository.findUsersWithProfiles(hasProfile)
    }
}
