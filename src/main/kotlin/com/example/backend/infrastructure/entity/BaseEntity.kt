package com.example.backend.infrastructure.entity

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 共通の監査フィールドを持つベースエンティティ
 */
@MappedSuperclass
abstract class BaseEntity {
    
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
    
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
        protected set
    
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }
} 