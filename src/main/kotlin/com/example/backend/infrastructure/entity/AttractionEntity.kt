package com.example.backend.infrastructure.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Size

/**
 * アトラクション情報を表すJPAエンティティ
 *
 * @property id アトラクションの一意なID
 * @property name アトラクション名
 * @property description アトラクションの説明
 * @property waitTime 待ち時間（分）
 * @property isActive アクティブかどうか
 */
@Entity
@Table(name = "attractions")
class AttractionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Size(min = 1, max = 255)
    @Column(nullable = false, length = 255, unique = true)
    var name: String,

    @Size(max = 1000)
    @Column(length = 1000)
    var description: String? = null,

    @Column(name = "wait_time", nullable = false)
    var waitTime: Int = 0,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true

) : BaseEntity() {
    
    // JPA用のデフォルトコンストラクタ
    constructor() : this(
        id = null,
        name = "",
        description = null,
        waitTime = 0,
        isActive = true
    )
    
    fun copy(
        id: Long? = this.id,
        name: String = this.name,
        description: String? = this.description,
        waitTime: Int = this.waitTime,
        isActive: Boolean = this.isActive
    ): AttractionEntity {
        return AttractionEntity(
            id = id,
            name = name,
            description = description,
            waitTime = waitTime,
            isActive = isActive
        )
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as AttractionEntity
        return id == other.id
    }
    
    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
    
    override fun toString(): String {
        return "AttractionEntity(id=$id, name='$name', waitTime=$waitTime, isActive=$isActive)"
    }
}