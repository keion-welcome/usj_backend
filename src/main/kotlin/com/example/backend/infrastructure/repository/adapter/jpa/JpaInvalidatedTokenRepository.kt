package com.example.backend.infrastructure.repository.adapter.jpa

import com.example.backend.infrastructure.entity.InvalidatedTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface JpaInvalidatedTokenRepository : JpaRepository<InvalidatedTokenEntity, String> {
    fun existsByToken(token: String): Boolean
    
    @Modifying
    @Query("DELETE FROM InvalidatedTokenEntity i WHERE i.expiryDate < :now")
    fun deleteExpiredTokens(now: Instant): Int
}