package com.example.backend.infrastructure.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * JWTトークンを検証し、ユーザー認証を行うためのフィルター。
 * 全てのリクエストに対して一度だけ実行される。
 */
@Component
class JwtAuthFilter(
    private val jwtUtil: JwtUtil,
    // Spring Securityがユーザー情報をロードするために使用するサービス
    // これを実装したBeanが別途必要になる
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // "Authorization" ヘッダーからトークンを取得
        val authHeader = request.getHeader("Authorization")

        // ヘッダーがない、または "Bearer " で始まらない場合は処理を続行
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        // "Bearer " の部分を除いたトークン文字列を取得
        val jwt = authHeader.substring(7)
        // トークンからユーザー名（この場合はメールアドレス）を抽出
        val userEmail = jwtUtil.getSubject(jwt)

        // SecurityContextに認証情報がまだ設定されていない場合
        if (SecurityContextHolder.getContext().authentication == null) {
            // メールアドレスからユーザー情報をDB経由で取得
            val userDetails = this.userDetailsService.loadUserByUsername(userEmail)

            // トークンが有効な場合
            if (jwtUtil.validateToken(jwt, userDetails.username)) {
                // 認証トークンを作成
                val authToken = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null, // パスワードは不要
                    userDetails.authorities
                )
                // リクエスト詳細を設定
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                // SecurityContextに認証情報を設定
                SecurityContextHolder.getContext().authentication = authToken
            }
        }
        // 次のフィルターへ処理を渡す
        filterChain.doFilter(request, response)
    }
}