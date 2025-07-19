package com.example.backend.api.controller

import com.example.backend.api.dto.request.CreateProfileRequest
import com.example.backend.api.dto.response.ProfileResponse
import com.example.backend.api.mapper.ProfileMapper
import com.example.backend.usecase.impl.CreateProfileUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * プロフィール関連のAPIコントローラー
 *
 * @property createProfileUseCase プロフィール作成ユースケース
 */
@RestController
@RequestMapping("/api/profiles")
class ProfileController(private val createProfileUseCase: CreateProfileUseCase) {

    /**
     * プロフィールを作成する
     *
     * @param request プロフィール作成リクエスト
     * @return 作成されたプロフィールのレスポンス
     */
    @PostMapping
    fun createProfile(@RequestBody request: CreateProfileRequest): ResponseEntity<ProfileResponse> {
        val profile = ProfileMapper.toModel(request)
        val createdProfile = createProfileUseCase.createProfile(profile)
        val response = ProfileMapper.toResponse(createdProfile)
        return ResponseEntity(response, HttpStatus.CREATED)
    }
}
