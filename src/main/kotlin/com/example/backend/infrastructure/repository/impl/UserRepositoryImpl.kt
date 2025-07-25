package com.example.backend.infrastructure.repository.impl

import com.example.backend.domain.model.User
import com.example.backend.infrastructure.entity.UserEntity
import com.example.backend.infrastructure.repository.jpa.JpaUserRepository
import com.example.backend.usecase.gateway.UserRepositoryPort
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
            username = user.username,
            email = user.email,
            password = user.password
        )
        val saved = jpaRepository.save(entity)
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
}
