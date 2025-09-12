package com.example.backend.infrastructure.repository.impl

import com.example.backend.domain.model.User
import com.example.backend.infrastructure.entity.UserEntity
import com.example.backend.infrastructure.repository.jpa.JpaUserRepository
import com.example.backend.usecase.gateway.UserRepositoryPort
import com.example.backend.shared.util.Uuid7Utils
import org.springframework.stereotype.Repository

/**
 * JPAを使ってユーザーをDBに保存・検索する実装クラス。
 * UserRepositoryPortを実装する。
 */
@Repository
class UserRepositoryImpl(
    private val jpaRepository: JpaUserRepository // JPAリポジトリ（Spring Data JPA）
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
}
