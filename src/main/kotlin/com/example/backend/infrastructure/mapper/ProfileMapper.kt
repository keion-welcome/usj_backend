package com.example.backend.infrastructure.mapper

import com.example.backend.domain.model.Profile
import com.example.backend.infrastructure.entity.ProfileEntity
import com.example.backend.infrastructure.entity.UserEntity
import org.springframework.stereotype.Component

/**
 * ProfileドメインモデルとProfileEntityの変換を行うマッパー
 */
@Component
class ProfileMapper {

    /**
     * ProfileドメインモデルをProfileEntityに変換する
     *
     * @param profile ドメインモデル
     * @param userEntity 関連するユーザーエンティティ
     * @return ProfileEntity
     */
    fun toEntity(profile: Profile, userEntity: UserEntity): ProfileEntity {
        return ProfileEntity(
            id = profile.id,
            user = userEntity,
            nickname = profile.nickname,
            gender = profile.gender,
            birthdate = profile.birthdate,
            area = profile.area,
            occupation = profile.occupation,
            hasAnnualPass = profile.hasAnnualPass
        )
    }

    /**
     * ProfileEntityをProfileドメインモデルに変換する
     *
     * @param entity エンティティ
     * @return Profile
     */
    fun toModel(entity: ProfileEntity): Profile {
        return Profile(
            id = entity.id,
            userId = entity.user.id,
            nickname = entity.nickname,
            gender = entity.gender,
            birthdate = entity.birthdate,
            area = entity.area,
            occupation = entity.occupation,
            hasAnnualPass = entity.hasAnnualPass
        )
    }
} 