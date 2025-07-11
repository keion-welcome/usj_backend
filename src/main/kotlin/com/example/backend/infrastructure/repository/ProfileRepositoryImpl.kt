package com.example.backend.infrastructure.repository

import com.example.backend.domain.model.Profile
import com.example.backend.infrastructure.entity.ProfileEntity
import com.example.backend.usecase.gateway.ProfileRepositoryPort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * プロフィールリポジトリのJPA実装
 */
@Repository
interface JpaProfileRepository : JpaRepository<ProfileEntity, Long>

/**
 * プロフィールリポジトリポートの実装クラス
 *
 * @property jpaProfileRepository JpaProfileRepository
 */
@Repository
class ProfileRepositoryImpl(
    private val jpaProfileRepository: JpaProfileRepository
) : ProfileRepositoryPort {

    /**
     * プロフィールを保存する
     *
     * @param profile 保存するプロフィール
     * @return 保存されたプロフィール
     */
    override fun save(profile: Profile): Profile {
        val profileEntity = profile.toEntity()
        val savedProfileEntity = jpaProfileRepository.save(profileEntity)
        return savedProfileEntity.toModel()
    }

    /**
     * ProfileドメインモデルをProfileEntityに変換する
     *
     * @return ProfileEntity
     */
    private fun Profile.toEntity(): ProfileEntity {
        return ProfileEntity(
            id = this.id,
            userId = this.userId,
            nickname = this.nickname,
            gender = this.gender,
            birthdate = this.birthdate,
            area = this.area,
            occupation = this.occupation,
            hasAnnualPass = this.hasAnnualPass
        )
    }

    /**
     * ProfileEntityをProfileドメインモデルに変換する
     *
     * @return Profile
     */
    private fun ProfileEntity.toModel(): Profile {
        return Profile(
            id = this.id,
            userId = this.userId,
            nickname = this.nickname,
            gender = this.gender,
            birthdate = this.birthdate,
            area = this.area,
            occupation = this.occupation,
            hasAnnualPass = this.hasAnnualPass
        )
    }
}
