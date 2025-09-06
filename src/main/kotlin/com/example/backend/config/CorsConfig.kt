package com.example.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * CORS（Cross-Origin Resource Sharing）設定を管理するクラス。
 * フロントエンドアプリケーションからのクロスオリジンリクエストを許可します。
 */
@Configuration
class CorsConfig {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        
        // 許可するオリジン（フロントエンドのURL）
        configuration.allowedOriginPatterns = listOf(
            "http://localhost:3000",  // React開発サーバー
            "http://localhost:5173",  // Vite開発サーバー
            "http://127.0.0.1:3000",
            "http://127.0.0.1:5173"
        )
        
        // 許可するHTTPメソッド
        configuration.allowedMethods = listOf(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        )
        
        // 許可するヘッダー
        configuration.allowedHeaders = listOf("*")
        
        // 認証情報（Cookieなど）を含むリクエストを許可
        configuration.allowCredentials = true
        
        // プリフライトリクエストのキャッシュ時間（秒）
        configuration.maxAge = 3600L
        
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        
        return source
    }
} 