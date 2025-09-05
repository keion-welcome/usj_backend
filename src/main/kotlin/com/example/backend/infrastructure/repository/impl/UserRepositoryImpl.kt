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
            id = user.id,
            email = user.email,
            passwordHash = user.passwordHash
        )
        val saved = jpaRepository.save(entity)
        return User(
            id = saved.id,
            email = saved.email,
            passwordHash = saved.passwordHash
        )
    }

    /**
     * メールアドレスでユーザーを検索
     */
    override fun findByEmail(email: String): User? {
        return jpaRepository.findByEmail(email)?.let {
            User(
                id = it.id,
                email = it.email,
                passwordHash = it.passwordHash
            )
        }
    }

    /**
     * IDでユーザーを検索
     */
    override fun findById(id: String): User? {
        return jpaRepository.findById(id).orElse(null)?.let {
            User(
                id = it.id,
                email = it.email,
                passwordHash = it.passwordHash
            )
        }
    }
} 