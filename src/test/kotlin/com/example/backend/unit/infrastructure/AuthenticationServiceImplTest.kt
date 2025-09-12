package com.example.backend.unit.infrastructure

import com.example.backend.infrastructure.security.service.AuthenticationServiceImpl
import com.example.backend.infrastructure.security.jwt.JwtUtil
import com.example.backend.usecase.gateway.UserRepositoryPort
import com.example.backend.domain.model.User
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows

/**
 * AuthenticationServiceImplの単体テスト
 * 
 * 認証サービスの核となる機能をテストします。
 * 各テストケースでは、テストの意図に特化したデータを個別定義し、
 * テスト実行時にも何をテストしているかが明確になるよう設計しています。
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("認証サービス")
class AuthenticationServiceImplTest {

    @Mock
    private lateinit var authenticationManager: AuthenticationManager

    @Mock
    private lateinit var jwtUtil: JwtUtil

    @Mock
    private lateinit var userRepository: UserRepositoryPort

    @Mock
    private lateinit var authentication: Authentication

    @InjectMocks
    private lateinit var authenticationService: AuthenticationServiceImpl

    @Test
    @DisplayName("正常系: 有効な認証情報でユーザー認証が成功する")
    fun `should authenticate user successfully with valid credentials`() {
        // Given - 有効な認証情報での認証成功のテストデータ
        // テストケース: 登録済みユーザーの正しいメールアドレスとパスワードでの認証
        val validUserEmail = "registered.user@example.com"
        val validUserPassword = "UserValidPassword123!"
        
        // 認証対象の登録済みユーザー
        val registeredUser = User(
            id = 1L,
            userId = "registered_user_001",
            email = validUserEmail,
            password = "hashed_UserValidPassword123!"  // ハッシュ化済みパスワード
        )
        
        val expectedAuthenticatedUserId = registeredUser.userId!!

        // モック設定: メールアドレスでユーザー検索が成功
        given(userRepository.findByEmail(validUserEmail)).willReturn(registeredUser)
        // モック設定: Spring Security認証が成功
        given(authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(expectedAuthenticatedUserId, validUserPassword)
        )).willReturn(authentication)

        // When - 認証処理実行
        val actualAuthenticatedUserId = authenticationService.authenticate(validUserEmail, validUserPassword)

        // Then - 期待されるユーザーIDが返されることを検証
        assertThat(actualAuthenticatedUserId).isEqualTo(expectedAuthenticatedUserId)
        
        // 処理フローの検証
        verify(userRepository).findByEmail(validUserEmail)  // ユーザー検索実行確認
        verify(authenticationManager).authenticate(        // Spring Security認証実行確認
            UsernamePasswordAuthenticationToken(expectedAuthenticatedUserId, validUserPassword)
        )
    }

    @Test
    @DisplayName("異常系: 未登録のメールアドレスで認証を試行すると例外が発生する")
    fun `should throw exception when user not found by email`() {
        // Given - 未登録メールアドレスでの認証失敗のテストデータ
        // テストケース: データベースに存在しないメールアドレスでの認証試行
        val unregisteredEmail = "unregistered.user@example.com"
        val attemptedPassword = "SomePassword123!"

        // モック設定: メールアドレスでユーザー検索が失敗（null返却）
        given(userRepository.findByEmail(unregisteredEmail)).willReturn(null)

        // When & Then - 未登録メールアドレスによる例外発生を検証
        val actualException = assertThrows<IllegalArgumentException> {
            authenticationService.authenticate(unregisteredEmail, attemptedPassword)
        }

        // 期待されるエラーメッセージの検証
        val expectedErrorMessage = "Invalid credentials"
        assertThat(actualException.message).isEqualTo(expectedErrorMessage)
        
        // ユーザー検索が実行されたことを検証
        verify(userRepository).findByEmail(unregisteredEmail)
    }

    @Test
    @DisplayName("JWT生成: 有効なユーザーIDからJWTトークンが正常に生成される")
    fun `should generate JWT token successfully`() {
        // Given - JWTトークン生成のテストデータ
        // テストケース: 認証済みユーザーのIDからJWTトークンを生成
        val authenticatedUserId = "jwt_generation_user_001"
        val expectedGeneratedToken = "jwt.token.for.jwt_generation_user_001"

        // モック設定: JWTUtilでトークン生成が成功
        given(jwtUtil.generateToken(authenticatedUserId)).willReturn(expectedGeneratedToken)

        // When - JWTトークン生成実行
        val actualGeneratedToken = authenticationService.generateToken(authenticatedUserId)

        // Then - 期待されるJWTトークンが返されることを検証
        assertThat(actualGeneratedToken).isEqualTo(expectedGeneratedToken)
        
        // JWTUtilのgenerateTokenメソッドが呼ばれたことを検証
        verify(jwtUtil).generateToken(authenticatedUserId)
    }

    @Test
    @DisplayName("認証処理: Spring Security認証マネージャーと連携した認証処理")
    fun `should authenticate and work with authentication manager`() {
        // Given - Spring Security認証マネージャー連携のテストデータ
        // テストケース: AuthenticationManagerを使用した認証処理の連携確認
        val securityTestEmail = "security.manager@example.com"
        val securityTestPassword = "SecurityManagerPass!"
        
        // Spring Security認証テスト用ユーザー
        val securityTestUser = User(
            id = 10L,
            userId = "security_manager_user_001",
            email = securityTestEmail,
            password = "hashed_SecurityManagerPass!"
        )

        // モック設定: ユーザー検索が成功
        given(userRepository.findByEmail(securityTestEmail)).willReturn(securityTestUser)
        // モック設定: Spring Security認証マネージャーでの認証が成功
        given(authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(securityTestUser.userId, securityTestPassword)
        )).willReturn(authentication)

        // When - 認証処理実行（AuthenticationManager連携）
        authenticationService.authenticate(securityTestEmail, securityTestPassword)

        // Then - AuthenticationManagerが適切に呼び出されることを検証
        verify(authenticationManager).authenticate(
            UsernamePasswordAuthenticationToken(securityTestUser.userId, securityTestPassword)
        )
    }

    @Test
    @DisplayName("異常系: Spring Security認証処理で例外が発生した場合の処理")
    fun `should handle authentication failure gracefully`() {
        // Given - Spring Security認証失敗のテストデータ
        // テストケース: 正しいユーザーIDだが認証処理で例外が発生
        val authFailureTestEmail = "auth.failure@example.com"
        val wrongPassword = "WrongPassword123!"
        
        // 認証失敗テスト用ユーザー（正しいユーザー情報）
        val authFailureTestUser = User(
            id = 20L,
            userId = "auth_failure_user_001",
            email = authFailureTestEmail,
            password = "hashed_correct_password"  // 正しいハッシュ化パスワード
        )

        // モック設定: ユーザー検索は成功
        given(userRepository.findByEmail(authFailureTestEmail)).willReturn(authFailureTestUser)
        // モック設定: Spring Security認証で例外発生（パスワード不一致など）
        given(authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(authFailureTestUser.userId, wrongPassword)
        )).willThrow(RuntimeException("Authentication failed"))

        // When & Then - Spring Security認証失敗による例外発生を検証
        assertThrows<RuntimeException> {
            authenticationService.authenticate(authFailureTestEmail, wrongPassword)
        }

        // 処理フローの検証
        verify(userRepository).findByEmail(authFailureTestEmail)               // ユーザー検索実行確認
        verify(authenticationManager).authenticate(                            // 認証処理実行確認
            UsernamePasswordAuthenticationToken(authFailureTestUser.userId, wrongPassword)
        )
    }
} 