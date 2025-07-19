package com.example.backend.infrastructure.repository.jpa

import com.example.backend.infrastructure.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data JPA のリポジトリインターフェース。
 * 自動的に実装される。
 */
@Repository
interface JpaUserRepository : JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity?
}