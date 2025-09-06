package com.example.backend.infrastructure.security.jwt

import com.example.backend.usecase.gateway.InvalidatedTokenRepositoryPort
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
    private val userDetailsService: UserDetailsService,
    private val invalidatedTokenRepositoryPort: InvalidatedTokenRepositoryPort
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        val isOptions = request.method.equals("OPTIONS", ignoreCase = true)
        return isOptions ||
               path.startsWith("/api/auth/") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path == "/error"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val jwt = authHeader.substring(7)

        try {
            // トークンが無効化されているか確認
            if (invalidatedTokenRepositoryPort.exists(jwt)) {
                filterChain.doFilter(request, response)
                return
            }

            val userId = jwtUtil.getSubject(jwt)

            if (SecurityContextHolder.getContext().authentication == null) {
                val userDetails = this.userDetailsService.loadUserByUsername(userId)

                if (jwtUtil.validateToken(jwt, userDetails.username)) {
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities
                    )
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }
        } catch (e: Exception) {
            // JWTトークンの処理で例外が発生した場合はログに記録してスキップ
            logger.debug("JWT token processing failed: ${e.message}")
        }
        
        filterChain.doFilter(request, response)
    }
}