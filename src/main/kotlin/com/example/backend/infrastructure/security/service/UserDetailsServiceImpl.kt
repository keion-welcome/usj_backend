package com.example.backend.infrastructure.security.service

import com.example.backend.infrastructure.security.repository.SecurityUserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Spring Securityがユーザー名（今回はメールアドレス）でユーザーを検索するためのサービス。
 * セキュリティ専用のリポジトリを使用して、アプリケーション層との依存関係を分離。
 * 
 * 注意：Spring Securityの標準インターフェースにより、パラメータ名は`username`ですが、
 * 実際にはメールアドレスを使用しています。
 */
@Service
class UserDetailsServiceImpl(
    private val securityUserRepository: SecurityUserRepository
) : UserDetailsService {

    /**
     * メールアドレスを元にユーザー情報をロードする。
     * 
     * @param username 実際にはメールアドレス（Spring Securityの標準インターフェースのため）
     * @return UserDetailsオブジェクト
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException ユーザーが見つからない場合
     */
    override fun loadUserByUsername(username: String): UserDetails {
        // usernameパラメータは実際にはメールアドレス
        val securityUser = securityUserRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("User not found with email: $username")

        // Spring Securityが扱うUserオブジェクトを生成して返す
        return User(securityUser.email, securityUser.password, emptyList()) // authoritiesは空でOK
    }
}