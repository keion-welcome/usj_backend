package com.example.backend.infrastructure.repository.impl

import com.example.backend.domain.model.Profile
import com.example.backend.infrastructure.entity.UserEntity
import com.example.backend.infrastructure.mapper.ProfileMapper
import com.example.backend.infrastructure.repository.jpa.JpaProfileRepository
import com.example.backend.infrastructure.repository.jpa.JpaUserRepository
import com.example.backend.usecase.gateway.ProfileRepositoryPort
import org.springframework.stereotype.Repository

/**
 * プロフィールリポジトリポートの実装クラス
 *
 * @property jpaProfileRepository JPAプロフィールリポジトリ
 * @property jpaUserRepository JPAユーザーリポジトリ
 * @property profileMapper プロフィールマッパー
 */
@Repository
class ProfileRepositoryImpl(
    private val jpaProfileRepository: JpaProfileRepository,
    private val jpaUserRepository: JpaUserRepository,
    private val profileMapper: ProfileMapper
) : ProfileRepositoryPort {

    /**
     * プロフィールを保存する
     *
     * @param profile 保存するプロフィール
     * @return 保存されたプロフィール
     */
    override fun save(profile: Profile): Profile {
        // ユーザーエンティティ取得
        val userEntity = jpaUserRepository.findById(profile.userId)
            .orElseThrow { IllegalArgumentException("User entity not found") }
        
        // エンティティ変換
        val profileEntity = profileMapper.toEntity(profile, userEntity)
        
        // 保存
        val savedProfileEntity = jpaProfileRepository.save(profileEntity)
        
        // モデル変換して返却
        return profileMapper.toModel(savedProfileEntity)
    }

    /**
     * ユーザーIDでプロフィールを検索する
     *
     * @param userId ユーザーID
     * @return プロフィール（存在しない場合はnull）
     */
    override fun findByUserId(userId: Long): Profile? {
        return jpaProfileRepository.findByUserId(userId)?.let { profileMapper.toModel(it) }
    }
}
