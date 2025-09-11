package com.example.backend.infrastructure.repository.impl

import com.example.backend.domain.model.User
import com.example.backend.infrastructure.entity.UserEntity
import com.example.backend.infrastructure.repository.adapter.jpa.JpaUserRepository
import com.example.backend.infrastructure.repository.adapter.jdbc.JdbcUserRepository
import com.example.backend.usecase.gateway.UserRepositoryPort
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
            username = user.username,
            email = user.email,
            password = user.password
            // userIdは@PrePersistで自動生成される
        )
        val saved = jpaRepository.save(entity)
        
        // UserID生成の検証（デバッグ強化）
        requireNotNull(saved.userId) { 
            "Critical error: UserEntity.userId generation failed for user: ${saved.username}. " +
            "This indicates a problem with @PrePersist or database constraints." 
        }
        require(saved.userId!!.isNotBlank()) { 
            "Critical error: UserEntity.userId is blank for user: ${saved.username}" 
        }
        
        return User(saved.id, saved.userId, saved.username, saved.email, saved.password)
    }

    /**
     * メールアドレスでユーザーを検索
     */
    override fun findByEmail(email: String): User? {
        return jpaRepository.findByEmail(email)?.let {
            User(it.id, it.userId, it.username, it.email, it.password)
        }
    }

    /**
     * IDでユーザーを検索
     */
    override fun findById(id: Long): User? {
        return jpaRepository.findById(id).orElse(null)?.let {
            User(it.id, it.userId, it.username, it.email, it.password)
        }
    }
    
    /**
     * ユーザーID（UUID）でユーザーを検索
     */
    override fun findByUserId(userId: String): User? {
        return jpaRepository.findByUserId(userId)?.let {
            User(it.id, it.userId, it.username, it.email, it.password)
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
     * 高速メール検索（JDBC使用）
     */
    fun fastEmailSearch(emailPattern: String): List<User> {
        return jdbcRepository.searchUsersByEmail(emailPattern)
    }
    
    /**
     * 全ユーザーを取得（JDBC使用）
     */
    fun getAllUsers(): List<User> {
        return jdbcRepository.findAll()
    }
    
    /**
     * ユーザーを作成（JDBC使用）
     */
    fun createUserWithJdbc(user: User): User {
        return jdbcRepository.createUser(user)
    }
    
    /**
     * ユーザーを更新（JDBC使用）
     */
    fun updateUserWithJdbc(user: User): User {
        return jdbcRepository.updateUser(user)
    }
    
    /**
     * ユーザーを削除（JDBC使用）
     */
    fun deleteUserWithJdbc(userId: String): Boolean {
        return jdbcRepository.deleteUser(userId)
    }
}
