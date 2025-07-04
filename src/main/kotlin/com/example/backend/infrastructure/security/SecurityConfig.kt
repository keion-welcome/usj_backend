package com.example.backend.infrastructure.security.config

import com.example.backend.infrastructure.security.JwtAuthFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * Spring Securityの全体的な設定を行うクラス。
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    /**
     * HTTPリクエストに対するセキュリティ設定を定義します。
     */
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // CSRF保護を無効化（JWT利用のため）
            .csrf { it.disable() }
            // URLごとの認可設定
            .authorizeHttpRequests { auth ->
                auth
                    // 認証・登録APIとSwagger関連は誰でもアクセス可能
                    .requestMatchers(
                        "/api/auth/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                    ).permitAll()
                    // その他のリクエストはすべて認証が必要
                    .anyRequest().authenticated()
            }
            // セッション管理をステートレスに設定（JWT利用のため）
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            // 作成したJWT認証フィルターを、標準の認証フィルターの前に配置
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}