package com.example.backend.unit.usecase

import com.example.backend.api.dto.response.AuthResponse
import com.example.backend.usecase.impl.LoginUseCase
import com.example.backend.usecase.gateway.AuthenticationPort
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows

/**
 * LoginUseCaseの単体テスト
 * 
 * ログインユースケースのビジネスロジックをテストします。
 * 各テストケースでは、テストの意図に特化したデータを個別定義し、
 * テスト実行時にも何をテストしているかが明確になるよう設計しています。
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("ログインユースケース")
class LoginUseCaseTest {

    @Mock
    private lateinit var authenticationPort: AuthenticationPort

    @InjectMocks
    private lateinit var loginUseCase: LoginUseCase

    @Test
    @DisplayName("正常系: 有効な認証情報でログインが成功し、JWTトークンが返される")
    fun `should execute login successfully and return auth token`() {
        // Given - 正常なログインのためのテストデータ
        // テストケース: 有効なメールアドレスとパスワードでのログイン成功
        val validLoginRequest = com.example.backend.api.dto.request.LoginRequest(
            email = "valid.user@example.com",     // 登録済みユーザーのメールアドレス
            password = "ValidUserPassword123!"   // 正しいパスワード
        )
        
        // 期待値: 認証成功時に返されるユーザーID
        val expectedAuthenticatedUserId = "authenticated_user_12345"
        // 期待値: ログイン成功時に返されるJWTトークン
        val expectedLoginToken = "jwt_login_token_for_authenticated_user_12345"

        // モック設定: 認証処理が成功してユーザーIDが返される
        given(authenticationPort.authenticate(validLoginRequest.email, validLoginRequest.password))
            .willReturn(expectedAuthenticatedUserId)
        // モック設定: JWTトークン生成が成功
        given(authenticationPort.generateToken(expectedAuthenticatedUserId))
            .willReturn(expectedLoginToken)

        // When - ログイン処理実行
        val actualLoginResult = loginUseCase.execute(validLoginRequest)

        // Then - 期待されるJWTトークンが返されることを検証
        assertThat(actualLoginResult).isNotNull
        assertThat(actualLoginResult.token).isEqualTo(expectedLoginToken)
        assertThat(actualLoginResult).isInstanceOf(AuthResponse::class.java)

        // 処理フローの検証
        verify(authenticationPort).authenticate(validLoginRequest.email, validLoginRequest.password)  // 認証処理実行確認
        verify(authenticationPort).generateToken(expectedAuthenticatedUserId)                         // トークン生成処理実行確認
    }

    @Test
    @DisplayName("異常系: 認証処理で例外が発生した場合の処理")
    fun `should handle authentication failure`() {
        // Given - 認証失敗のテストデータ
        // テストケース: 無効な認証情報での認証処理失敗
        val invalidLoginRequest = com.example.backend.api.dto.request.LoginRequest(
            email = "invalid.credentials@example.com",    // 無効なメールアドレス
            password = "WrongPassword123!"                // 間違ったパスワード
        )

        // モック設定: 認証処理で例外発生
        given(authenticationPort.authenticate(invalidLoginRequest.email, invalidLoginRequest.password))
            .willThrow(IllegalArgumentException("Invalid credentials"))

        // When & Then - 認証失敗による例外発生を検証
        val actualAuthException = assertThrows<IllegalArgumentException> {
            loginUseCase.execute(invalidLoginRequest)
        }

        // 期待されるエラーメッセージの検証
        val expectedAuthErrorMessage = "Invalid credentials"
        assertThat(actualAuthException.message).isEqualTo(expectedAuthErrorMessage)
        
        // 認証処理が実行されたことを検証
        verify(authenticationPort).authenticate(invalidLoginRequest.email, invalidLoginRequest.password)
    }

    @Test
    @DisplayName("異常系: JWTトークン生成処理で例外が発生した場合の処理")
    fun `should handle token generation failure`() {
        // Given - JWTトークン生成失敗のテストデータ
        // テストケース: 認証は成功したがトークン生成で例外が発生
        val tokenErrorLoginRequest = com.example.backend.api.dto.request.LoginRequest(
            email = "token.error@example.com",
            password = "TokenErrorPassword!"
        )
        val authenticatedUserIdForTokenError = "token_error_user_001"

        // モック設定: 認証処理は成功
        given(authenticationPort.authenticate(tokenErrorLoginRequest.email, tokenErrorLoginRequest.password))
            .willReturn(authenticatedUserIdForTokenError)
        // モック設定: JWTトークン生成で例外発生
        given(authenticationPort.generateToken(authenticatedUserIdForTokenError))
            .willThrow(RuntimeException("Token generation failed"))

        // When & Then - JWTトークン生成失敗による例外発生を検証
        val actualTokenException = assertThrows<RuntimeException> {
            loginUseCase.execute(tokenErrorLoginRequest)
        }

        // 期待されるエラーメッセージの検証
        val expectedTokenErrorMessage = "Token generation failed"
        assertThat(actualTokenException.message).isEqualTo(expectedTokenErrorMessage)
        
        // 処理フローの検証
        verify(authenticationPort).authenticate(tokenErrorLoginRequest.email, tokenErrorLoginRequest.password)  // 認証処理実行確認
        verify(authenticationPort).generateToken(authenticatedUserIdForTokenError)                              // トークン生成処理実行確認
    }

    @Test
    @DisplayName("複数ユーザー: 異なるユーザーの認証情報が正しく処理される")
    fun `should process different user credentials correctly`() {
        // Given - 複数ユーザーログインのテストデータ
        // テストケース: 異なるユーザーの認証情報が正しく区別されて処理される
        val firstUserEmail = "first.user@example.com"
        val secondUserEmail = "second.user@example.com"
        val firstUserPassword = "FirstUserPassword!"
        val secondUserPassword = "SecondUserPassword!"
        
        val firstUserId = "first_user_001"
        val secondUserId = "second_user_002"
        val firstUserToken = "jwt.token.for.first_user_001"
        val secondUserToken = "jwt.token.for.second_user_002"

        // 第1ユーザーのログインリクエスト
        val firstUserLoginRequest = com.example.backend.api.dto.request.LoginRequest(
            email = firstUserEmail,
            password = firstUserPassword
        )
        
        // 第2ユーザーのログインリクエスト
        val secondUserLoginRequest = com.example.backend.api.dto.request.LoginRequest(
            email = secondUserEmail,
            password = secondUserPassword
        )

        // モック設定: 第1ユーザーの認証とトークン生成
        given(authenticationPort.authenticate(firstUserEmail, firstUserPassword)).willReturn(firstUserId)
        given(authenticationPort.generateToken(firstUserId)).willReturn(firstUserToken)
        
        // モック設定: 第2ユーザーの認証とトークン生成
        given(authenticationPort.authenticate(secondUserEmail, secondUserPassword)).willReturn(secondUserId)
        given(authenticationPort.generateToken(secondUserId)).willReturn(secondUserToken)

        // When - 両ユーザーのログイン処理実行
        val firstUserResult = loginUseCase.execute(firstUserLoginRequest)
        val secondUserResult = loginUseCase.execute(secondUserLoginRequest)

        // Then - 各ユーザーに適切なトークンが返されることを検証
        assertThat(firstUserResult.token).isEqualTo(firstUserToken)
        assertThat(secondUserResult.token).isEqualTo(secondUserToken)
        // 異なるユーザーには異なるトークンが発行されることを検証
        assertThat(firstUserResult.token).isNotEqualTo(secondUserResult.token)
    }

    @Test
    @DisplayName("統合: ログイン処理が正しい順序で実行されることの検証")
    fun `should validate request processing flow`() {
        // Given - 処理フロー検証のテストデータ
        // テストケース: 認証→トークン生成の順序で処理が実行される
        val flowValidationLoginRequest = com.example.backend.api.dto.request.LoginRequest(
            email = "flow.validation@example.com",
            password = "FlowValidationPassword!"
        )
        val flowValidationUserId = "flow_validation_user_001"
        val flowValidationToken = "jwt.flow.validation.token"

        // モック設定: 認証処理が成功
        given(authenticationPort.authenticate(flowValidationLoginRequest.email, flowValidationLoginRequest.password))
            .willReturn(flowValidationUserId)
        // モック設定: JWTトークン生成が成功
        given(authenticationPort.generateToken(flowValidationUserId))
            .willReturn(flowValidationToken)

        // When - ログイン処理実行（処理フロー検証対象）
        val actualFlowResult = loginUseCase.execute(flowValidationLoginRequest)

        // Then - 正しい順序で処理が実行されることを検証
        // 処理順序の厳密な検証（InOrderを使用）
        val processingOrder = org.mockito.kotlin.inOrder(authenticationPort)
        processingOrder.verify(authenticationPort).authenticate(flowValidationLoginRequest.email, flowValidationLoginRequest.password)  // 1. 認証処理
        processingOrder.verify(authenticationPort).generateToken(flowValidationUserId)                                                   // 2. トークン生成
        
        // 期待されるトークンが返されることを検証
        assertThat(actualFlowResult.token).isEqualTo(flowValidationToken)
    }
} 