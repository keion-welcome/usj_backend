package com.example.backend.config

import com.example.backend.infrastructure.security.jwt.JwtAuthFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.http.MediaType
import jakarta.servlet.http.HttpServletResponse

/**
 * Spring Securityの全体的な設定を行うクラス。
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val corsConfigurationSource: CorsConfigurationSource,
    private val jwtAuthFilter: JwtAuthFilter,
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    /**
     * HTTPリクエストに対するセキュリティ設定を定義します。
     */
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // CORS設定を有効化 (corsConfigurationSource Beanを利用)
            .cors(Customizer.withDefaults())
            // CSRF保護を無効化（JWT利用のため）
            .csrf { it.disable() }
            // CORS設定を有効化
            .cors { it.configurationSource(corsConfigurationSource) }
            .authorizeHttpRequests { auth ->
                auth
                    // OPTIONSメソッドはすべて許可 (プリフライトリクエストのため)
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    // 認証・登録APIとSwagger関連は誰でもアクセス可能
                    .requestMatchers(
                        "/api/auth/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/error"
                    ).permitAll()
                    // その他のリクエストはすべて認証が必要
                    .anyRequest().authenticated()
            }
            // セッション管理をステートレスに設定（JWT利用のため）
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            // 認証エラー時の処理
            .exceptionHandling { exceptions ->
                exceptions.authenticationEntryPoint { _, response, _ ->
                    response.contentType = MediaType.APPLICATION_JSON_VALUE
                    response.status = HttpServletResponse.SC_UNAUTHORIZED
                    response.writer.write("{" + "\"status\":401,\"error\":\"Unauthorized\"}" )
                }.accessDeniedHandler { _, response, _ ->
                    response.contentType = MediaType.APPLICATION_JSON_VALUE
                    response.status = HttpServletResponse.SC_FORBIDDEN
                    response.writer.write("{" + "\"status\":403,\"error\":\"Forbidden\"}" )
                }
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

}