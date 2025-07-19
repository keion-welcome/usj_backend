package com.example.backend.infrastructure.repository.jpa

import com.example.backend.infrastructure.entity.ProfileEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * プロフィールリポジトリのJPA実装
 */
@Repository
interface JpaProfileRepository : JpaRepository<ProfileEntity, Long> {
    
    /**
     * ユーザーIDでプロフィールを検索（効率的な実装）
     * 
     * @param userId ユーザーID
     * @return プロフィールエンティティ（存在しない場合はnull）
     */
    @Query("SELECT p FROM ProfileEntity p WHERE p.user.id = :userId")
    fun findByUserId(@Param("userId") userId: Long): ProfileEntity?
} 