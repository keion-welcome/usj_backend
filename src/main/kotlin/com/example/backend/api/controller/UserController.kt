package com.example.backend.api.controller

import com.example.backend.api.dto.response.UserResponse
import com.example.backend.usecase.gateway.UserRepositoryPort
import com.example.backend.infrastructure.repository.adapter.jpa.JpaProfileRepository
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

/**
 * ユーザー情報のAPIエンドポイントを提供するコントローラー
 */
@RestController
@RequestMapping("/api/users")
class UserController(
    private val userRepositoryPort: UserRepositoryPort,
    private val jpaProfileRepository: JpaProfileRepository
) {

    /**
     * 現在のユーザー情報を取得する
     *
     * @param userDetails 認証ユーザー情報
     * @return ユーザー情報
     */
    @GetMapping("/me")
    fun getCurrentUser(@AuthenticationPrincipal userDetails: UserDetails): UserResponse {
        val email = userDetails.username
        val user = userRepositoryPort.findByEmail(email) ?: throw Exception("User not found")
        val profile = jpaProfileRepository.findByUser_Id(user.userId!!)
        return UserResponse.from(user, profile)
    }
}