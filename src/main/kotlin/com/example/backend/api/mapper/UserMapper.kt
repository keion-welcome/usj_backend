package com.example.backend.api.mapper


import com.example.backend.api.dto.request.RegisterRequest
import com.example.backend.domain.model.User

/**
 * RegisterRequest（DTO）をUser（ドメイン）に変換するマッパー
 */
object UserMapper {

    /**
     * DTO → ドメインモデルへの変換
     * @param request 登録リクエストDTO
     * @param encodedPassword ハッシュ化されたパスワード
     * @return ドメインのUserモデル
     */
    fun toDomain(request: RegisterRequest, encodedPassword: String): User {
        return User(
            username = request.nickname,
            email = request.email,
            password = encodedPassword
        )
    }
}