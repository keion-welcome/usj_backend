package com.example.backend.infrastructure.repository.adapter.jpa

import com.example.backend.infrastructure.entity.ProfileEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaProfileRepository : JpaRepository<ProfileEntity, Long> {
    fun findByUser_Id(userId: String): ProfileEntity?
    fun existsByUser_Id(userId: String): Boolean
    fun deleteByUser_Id(userId: String)
}