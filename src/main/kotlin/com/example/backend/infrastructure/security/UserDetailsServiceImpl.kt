package com.example.backend.infrastructure.security

import com.example.backend.usecase.port.UserRepositoryPort
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Spring Securityがユーザー名（今回はメールアドレス）でユーザーを検索するためのサービス。
 */
@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepositoryPort
) : UserDetailsService {

    /**
     * メールアドレスを元にユーザー情報をロードする。
     * @param username 実際にはメールアドレス
     * @return UserDetailsオブジェクト
     * @throws UsernameNotFoundException ユーザーが見つからない場合
     */
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("User not found with email: $username")

        // Spring Securityが扱うUserオブジェクトを生成して返す
        return User(user.email, user.password, emptyList()) // authoritiesは空でOK
    }
}