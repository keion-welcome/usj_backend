package com.example.backend.usecase.port
import com.example.backend.domain.model.User

/**
 * ユースケース層が依存するリポジトリの抽象（ポート）。
 * ドメインのUserRepositoryとほぼ同じだが、ユースケース視点での定義。
 */
interface UserRepositoryPort {
    fun save(user: User): User
    fun findByEmail(email: String): User?
}