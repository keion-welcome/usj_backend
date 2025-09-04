package com.example.backend.unit.usecase

import com.example.backend.api.dto.request.RegisterRequest
import com.example.backend.usecase.impl.RegisterUseCase
import com.example.backend.usecase.gateway.UserRepositoryPort
import com.example.backend.infrastructure.security.jwt.JwtUtil
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import com.example.backend.domain.model.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.assertj.core.api.Assertions.assertThat

/**
 * RegisterUseCaseの単体テスト
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("ユーザー登録ユースケース")
class RegisterUseCaseTest {

    @Mock
    private lateinit var userRepository: UserRepositoryPort

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var jwtUtil: JwtUtil

    @InjectMocks
    private lateinit var registerUseCase: RegisterUseCase

    @Test
    @DisplayName("正常系: 有効なユーザー情報で登録が成功し、JWTトークンが返される")
    fun `should register user successfully and return auth token`() {
        // Given
        val request = RegisterRequest(
            email = "yamada.taro@example.com",
            password = "SecurePass123!"
        )
        
        val savedUser = User(
            id = "user_yamada_taro_001",
            email = "yamada.taro@example.com",
            passwordHash = "hashed_password"
        )
        
        val expectedToken = "jwt_token_yamada_taro_001"
        
        // モック設定
        whenever(userRepository.findByEmail("yamada.taro@example.com")).thenReturn(null)
        whenever(passwordEncoder.encode("SecurePass123!")).thenReturn("hashed_password")
        whenever(userRepository.save(any())).thenReturn(savedUser)
        whenever(jwtUtil.generateToken("user_yamada_taro_001")).thenReturn(expectedToken)

        // When
        val result = registerUseCase.execute(request)

        // Then
        assertThat(result.token).isEqualTo(expectedToken)
    }
}
