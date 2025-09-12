package com.example.backend.infrastructure.repository

import com.example.backend.domain.model.Recruitment
import com.example.backend.domain.model.RecruitmentParticipant
import com.example.backend.domain.model.RecruitmentStatus
import com.example.backend.infrastructure.entity.RecruitmentEntity
import com.example.backend.infrastructure.entity.RecruitmentParticipantEntity
import com.example.backend.infrastructure.repository.adapter.jpa.JpaRecruitmentRepository
import com.example.backend.infrastructure.repository.adapter.jpa.JpaRecruitmentParticipantRepository
import com.example.backend.infrastructure.repository.adapter.jpa.JpaUserRepository
import com.example.backend.infrastructure.repository.adapter.jdbc.JdbcRecruitmentRepository
import com.example.backend.usecase.gateway.RecruitmentRepositoryPort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 募集リポジトリ実装
 *
 * @property jpaRecruitmentRepository JpaRecruitmentRepository
 * @property jpaRecruitmentParticipantRepository JpaRecruitmentParticipantRepository
 */
@Repository
class RecruitmentRepositoryImpl(
    private val jpaRecruitmentRepository: JpaRecruitmentRepository,
    private val jpaRecruitmentParticipantRepository: JpaRecruitmentParticipantRepository,
    private val jpaUserRepository: JpaUserRepository,
    private val jdbcRecruitmentRepository: JdbcRecruitmentRepository
) : RecruitmentRepositoryPort {

    /**
     * 募集を作成する
     *
     * @param recruitment 作成する募集
     * @return 作成された募集
     */
    @Transactional
    override fun create(recruitment: Recruitment): Recruitment {
        val recruitmentEntity = recruitment.toEntity()
        val savedRecruitment = jpaRecruitmentRepository.save(recruitmentEntity)
        return savedRecruitment.toModel()
    }

    /**
     * 募集を更新する
     *
     * @param recruitment 更新する募集
     * @return 更新された募集
     */
    @Transactional
    override fun update(recruitment: Recruitment): Recruitment {
        val recruitmentEntity = recruitment.toEntity()
        val savedRecruitment = jpaRecruitmentRepository.save(recruitmentEntity)
        return savedRecruitment.toModel()
    }

    /**
     * 募集をIDで取得する
     *
     * @param id 募集ID
     * @return 募集、存在しない場合はnull
     */
    override fun findById(id: Long): Recruitment? {
        return jpaRecruitmentRepository.findById(id).orElse(null)?.toModel()
    }

    /**
     * すべての募集を取得する
     *
     * @return 募集のリスト
     */
    override fun findAll(): List<Recruitment> {
        return jpaRecruitmentRepository.findAllByOrderByCreatedAtDesc()
            .map { it.toModel() }
    }

    /**
     * ステータスで募集を検索する
     *
     * @param status 募集ステータス
     * @return 該当する募集のリスト
     */
    override fun findByStatus(status: RecruitmentStatus): List<Recruitment> {
        val entityStatus = status.toEntityStatus()
        return jpaRecruitmentRepository.findByStatusOrderByCreatedAtDesc(entityStatus)
            .map { it.toModel() }
    }

    /**
     * ユーザーが作成した募集を取得する
     *
     * @param userId ユーザーID
     * @return 該当する募集のリスト
     */
    override fun findByUserId(userId: Long): List<Recruitment> {
        return jpaRecruitmentRepository.findByUserId(userId)
            .map { it.toModel() }
    }

    /**
     * ユーザーが参加している募集を取得する
     *
     * @param userId ユーザーID
     * @return 該当する募集のリスト
     */
    override fun findByParticipantUserId(userId: Long): List<Recruitment> {
        return jpaRecruitmentRepository.findByParticipantUserId(userId)
            .map { it.toModel() }
    }

    /**
     * アトラクションに関連する募集を取得する
     *
     * @param attractionName アトラクション名
     * @return 該当する募集のリスト
     */
    override fun findByAttractionName(attractionName: String): List<Recruitment> {
        return jpaRecruitmentRepository.findByAttractionNameOrderByCreatedAtDesc(attractionName)
            .map { it.toModel() }
    }

    /**
     * 募集に参加する
     *
     * @param recruitmentId 募集ID
     * @param userId ユーザーID
     * @return 成功した場合はtrue
     */
    @Transactional
    override fun joinRecruitment(recruitmentId: Long, userId: Long): Boolean {
        // TODO: Convert Long userId to String for entity compatibility
        return try {
            // 既に参加しているかチェック
            val existing = jpaRecruitmentParticipantRepository
                .findByRecruitmentIdAndUserId(recruitmentId, userId.toString())
            if (existing != null) {
                return false
            }

            // 募集が存在するかチェック
            val recruitment = jpaRecruitmentRepository.findById(recruitmentId).orElse(null)
                ?: return false

            // 満員チェック
            val currentParticipants = jpaRecruitmentParticipantRepository.countByRecruitmentId(recruitmentId)
            if (currentParticipants >= recruitment.maxParticipants) {
                return false
            }

            // TODO: Fix user lookup - userId is Long but UserEntity uses String UUID
            // For now, skip user validation as the system has mixed ID types
            // val user = jpaUserRepository.findById(userId).orElse(null)
            //     ?: throw IllegalArgumentException("User not found")
            
            // 参加者を追加
            val participant = RecruitmentParticipantEntity(
                recruitmentId = recruitmentId,
                userId = userId.toString(), // Convert Long to String - TODO: Fix this properly
                recruitment = recruitment,
                user = null, // TODO: Fix user reference after resolving ID type mismatch
                joinedAt = LocalDateTime.now()
            )
            jpaRecruitmentParticipantRepository.save(participant)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 募集から退出する
     *
     * @param recruitmentId 募集ID
     * @param userId ユーザーID
     * @return 成功した場合はtrue
     */
    @Transactional
    override fun leaveRecruitment(recruitmentId: Long, userId: Long): Boolean {
        return try {
            jpaRecruitmentParticipantRepository.deleteByRecruitmentIdAndUserId(recruitmentId, userId.toString())
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 募集を削除する
     *
     * @param id 募集ID
     * @return 削除できた場合はtrue
     */
    @Transactional
    override fun deleteById(id: Long): Boolean {
        return try {
            jpaRecruitmentRepository.deleteById(id)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 募集ドメインモデルをエンティティに変換する
     *
     * @return 募集エンティティ
     */
    private fun Recruitment.toEntity(): RecruitmentEntity {
        return RecruitmentEntity(
            id = this.id,
            title = this.title,
            description = this.description,
            userId = this.userId,
            maxParticipants = this.maxParticipants,
            status = this.status.toEntityStatus(),
            expiresAt = this.expiresAt,
            attractionName = this.attractionName,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    /**
     * 募集エンティティをドメインモデルに変換する
     *
     * @return 募集ドメインモデル
     */
    private fun RecruitmentEntity.toModel(): Recruitment {
        val participants = jpaRecruitmentParticipantRepository.findByRecruitmentId(this.id!!)
            .map { RecruitmentParticipant(it.userId.toLongOrNull() ?: 0L, it.joinedAt) } // TODO: Fix - converting String back to Long

        // 注意: attractionsテーブルが削除されたため、attractionオブジェクトは常にnull
        val attraction: com.example.backend.domain.model.Attraction? = null

        return Recruitment(
            id = this.id,
            title = this.title,
            description = this.description,
            userId = this.userId,
            maxParticipants = this.maxParticipants,
            status = this.status.toModelStatus(),
            expiresAt = this.expiresAt,
            attractionName = this.attractionName,
            attraction = attraction,
            participants = participants,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    /**
     * ドメインステータスをエンティティステータスに変換
     */
    private fun RecruitmentStatus.toEntityStatus(): com.example.backend.infrastructure.entity.RecruitmentStatus {
        return when (this) {
            RecruitmentStatus.ACTIVE -> com.example.backend.infrastructure.entity.RecruitmentStatus.ACTIVE
            RecruitmentStatus.COMPLETED -> com.example.backend.infrastructure.entity.RecruitmentStatus.COMPLETED
            RecruitmentStatus.CANCELLED -> com.example.backend.infrastructure.entity.RecruitmentStatus.CANCELLED
        }
    }

    /**
     * エンティティステータスをドメインステータスに変換
     */
    private fun com.example.backend.infrastructure.entity.RecruitmentStatus.toModelStatus(): RecruitmentStatus {
        return when (this) {
            com.example.backend.infrastructure.entity.RecruitmentStatus.ACTIVE -> RecruitmentStatus.ACTIVE
            com.example.backend.infrastructure.entity.RecruitmentStatus.COMPLETED -> RecruitmentStatus.COMPLETED
            com.example.backend.infrastructure.entity.RecruitmentStatus.CANCELLED -> RecruitmentStatus.CANCELLED
        }
    }
    
    // === JDBC使用のメソッド ===
    
    /**
     * 募集を作成（JDBC使用）
     */
    fun createRecruitmentWithJdbc(recruitment: Recruitment): Recruitment {
        return jdbcRecruitmentRepository.createRecruitment(recruitment)
    }
    
    /**
     * 募集を更新（JDBC使用）
     */
    fun updateRecruitmentWithJdbc(recruitment: Recruitment): Recruitment {
        return jdbcRecruitmentRepository.updateRecruitment(recruitment)
    }
    
    /**
     * 募集を削除（JDBC使用）
     */
    fun deleteRecruitmentWithJdbc(id: Long): Boolean {
        return jdbcRecruitmentRepository.deleteRecruitment(id)
    }
    
    /**
     * 複雑な条件で募集を検索（JDBC使用）
     */
    fun searchRecruitmentsWithJdbc(
        title: String? = null,
        attractionName: String? = null,
        status: RecruitmentStatus? = null,
        maxParticipants: Int? = null
    ): List<Recruitment> {
        return jdbcRecruitmentRepository.findByComplexCriteria(title, attractionName, status, maxParticipants)
    }
    
    /**
     * 全募集を取得（JDBC使用）
     */
    fun getAllRecruitmentsWithJdbc(): List<Recruitment> {
        return jdbcRecruitmentRepository.findAll()
    }
}
