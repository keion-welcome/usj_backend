package com.example.backend.infrastructure.repository.adapter.jpa

import com.example.backend.infrastructure.entity.ProfileEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaProfileRepository : JpaRepository<ProfileEntity, Long> {
    fun findByUser_Id(userId: Long): ProfileEntity?
    fun findByUser_UserId(userUserId: String): ProfileEntity?
    fun existsByUser_Id(userId: Long): Boolean
    fun deleteByUser_Id(userId: Long)
}