package com.example.backend.infrastructure.repository.adapter.jpa

import com.example.backend.infrastructure.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaUserRepository : JpaRepository<UserEntity, String> {
    fun findByEmail(email: String): UserEntity?
}