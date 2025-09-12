package com.example.backend.infrastructure.repository.impl

import com.example.backend.domain.model.User
import com.example.backend.infrastructure.entity.UserEntity
import com.example.backend.infrastructure.repository.adapter.jpa.JpaUserRepository
import com.example.backend.infrastructure.repository.adapter.jdbc.JdbcUserRepository
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
            id = user.userId ?: throw IllegalArgumentException("User.userId must be provided (generated in use case)"),
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
        
        return User(
            id = user.id,
            userId = saved.id,
            email = saved.email,
            password = saved.password
        )
    }

    /**
     * メールアドレスでユーザーを検索
     */
    override fun findByEmail(email: String): User? {
        return jpaRepository.findByEmail(email)?.let {
            User(
                id = null,
                userId = it.id,
                email = it.email,
                password = it.password
            )
        }
    }

    /**
     * IDでユーザーを検索（Long型のID）
     */
    override fun findById(id: Long): User? {
        // Long型IDはUserEntityでは使用していないため、nullを返す
        // または適切なマッピングロジックを実装する必要がある
        return null
    }

    /**
     * ユーザーID（UUID）でユーザーを検索
     */
    override fun findByUserId(userId: String): User? {
        return jpaRepository.findById(userId).orElse(null)?.let {
            User(
                id = null,
                userId = it.id,
                email = it.email,
                password = it.password
            )
        }
    }
    
    // === JDBC使用の複雑な検索メソッド ===
    
    /**
     * 複雑な条件でユーザーを検索（JDBC使用）
     */
    fun searchUsers(
        email: String? = null,
        createdAfter: String? = null
    ): List<User> {
        return jdbcRepository.findByComplexCriteria(null, email, createdAfter)
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
