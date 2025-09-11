package com.example.backend.infrastructure.security.service

import com.example.backend.infrastructure.security.repository.SecurityUserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Spring Securityがユーザー名（このアプリケーションではメールアドレス）でユーザーを検索するためのサービス。
 */
@Service
class UserDetailsServiceImpl(
    private val securityUserRepository: SecurityUserRepository
) : UserDetailsService {

    /**
     * メールアドレスを元にユーザー情報をロードする。
     * 
     * @param username ユーザーのメールアドレス
     * @return UserDetailsオブジェクト
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException ユーザーが見つからない場合
     */
    override fun loadUserByUsername(username: String): UserDetails {
        // usernameパラメータはメールアドレスとして扱う
        val securityUser = securityUserRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("User not found with email: $username")

        // Spring Securityが扱うUserオブジェクトを生成して返す
        // 第1引数は、認証プリンシパルとして使われる「ユーザー名」
        return User(securityUser.email, securityUser.password, emptyList()) 
    }
}