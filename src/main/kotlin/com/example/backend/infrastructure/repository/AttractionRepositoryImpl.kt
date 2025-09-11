package com.example.backend.infrastructure.repository

import com.example.backend.domain.model.Attraction
import com.example.backend.infrastructure.entity.AttractionEntity
import com.example.backend.infrastructure.repository.adapter.jpa.JpaAttractionRepository
import com.example.backend.infrastructure.repository.adapter.jdbc.JdbcAttractionRepository
import com.example.backend.usecase.gateway.AttractionRepositoryPort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/**
 * アトラクションリポジトリ実装
 *
 * @property jpaAttractionRepository JpaAttractionRepository
 */
@Repository
class AttractionRepositoryImpl(
    private val jpaAttractionRepository: JpaAttractionRepository,
    private val jdbcAttractionRepository: JdbcAttractionRepository
) : AttractionRepositoryPort {

    override fun findAll(): List<Attraction> {
        return jpaAttractionRepository.findAll()
            .map { it.toModel() }
    }

    override fun findActiveAttractions(): List<Attraction> {
        return jpaAttractionRepository.findByIsActiveOrderByNameAsc(true)
            .map { it.toModel() }
    }

    override fun findById(id: Long): Attraction? {
        return jpaAttractionRepository.findById(id)
            .orElse(null)?.toModel()
    }

    override fun findByNameContaining(name: String): List<Attraction> {
        return jpaAttractionRepository.findByNameContainingIgnoreCaseAndIsActive(name, true)
            .map { it.toModel() }
    }

    @Transactional
    override fun updateWaitTime(id: Long, waitTime: Int): Attraction? {
        val entity = jpaAttractionRepository.findById(id).orElse(null) ?: return null
        val updatedEntity = entity.copy(waitTime = waitTime)
        val savedEntity = jpaAttractionRepository.save(updatedEntity)
        return savedEntity.toModel()
    }

    /**
     * アトラクションエンティティをドメインモデルに変換する
     *
     * @return アトラクションドメインモデル
     */
    private fun AttractionEntity.toModel(): Attraction {
        return Attraction(
            id = this.id,
            name = this.name,
            description = this.description,
            waitTime = this.waitTime,
            isActive = this.isActive,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    /**
     * アトラクションドメインモデルをエンティティに変換する
     *
     * @return アトラクションエンティティ
     */
    private fun Attraction.toEntity(): AttractionEntity {
        return AttractionEntity(
            id = this.id,
            name = this.name,
            description = this.description,
            waitTime = this.waitTime,
            isActive = this.isActive
        )
    }
    
    // === JDBC使用のメソッド ===
    
    /**
     * アトラクションを作成（JDBC使用）
     */
    fun createAttractionWithJdbc(attraction: Attraction): Attraction {
        return jdbcAttractionRepository.createAttraction(attraction)
    }
    
    /**
     * アトラクションを更新（JDBC使用）
     */
    fun updateAttractionWithJdbc(attraction: Attraction): Attraction {
        return jdbcAttractionRepository.updateAttraction(attraction)
    }
    
    /**
     * アトラクションを削除（JDBC使用）
     */
    fun deleteAttractionWithJdbc(id: Long): Boolean {
        return jdbcAttractionRepository.deleteAttraction(id)
    }
    
    /**
     * 名前で検索（JDBC使用）
     */
    fun findByNameWithJdbc(name: String): Attraction? {
        return jdbcAttractionRepository.findByName(name)
    }
    
    /**
     * 名前の部分一致検索（JDBC使用）
     */
    fun searchByNameWithJdbc(namePattern: String): List<Attraction> {
        return jdbcAttractionRepository.searchByName(namePattern)
    }
}