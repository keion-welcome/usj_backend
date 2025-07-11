package com.example.backend.api.mapper

import com.example.backend.api.dto.request.CreateProfileRequest
import com.example.backend.api.dto.response.ProfileResponse
import com.example.backend.domain.model.Profile

/**
 * プロフィールDTOとドメインモデルを変換するマッパー
 */
object ProfileMapper {

    /**
     * CreateProfileRequest DTOをProfileドメインモデルに変換する
     *
     * @param request CreateProfileRequest
     * @return Profile
     */
    fun toModel(request: CreateProfileRequest): Profile {
        return Profile(
            userId = request.userId,
            nickname = request.nickname,
            gender = request.gender,
            birthdate = request.birthdate,
            area = request.area,
            occupation = request.occupation,
            hasAnnualPass = request.hasAnnualPass
        )
    }

    /**
     * ProfileドメインモデルをProfileResponse DTOに変換する
     *
     * @param model Profile
     * @return ProfileResponse
     */
    fun toResponse(model: Profile): ProfileResponse {
        return ProfileResponse(
            id = model.id,
            userId = model.userId,
            nickname = model.nickname,
            gender = model.gender,
            birthdate = model.birthdate,
            area = model.area,
            occupation = model.occupation,
            hasAnnualPass = model.hasAnnualPass
        )
    }
}
