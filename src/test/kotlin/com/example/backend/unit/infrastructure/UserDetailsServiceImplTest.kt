package com.example.backend.unit.infrastructure

import com.example.backend.infrastructure.security.service.UserDetailsServiceImpl
import com.example.backend.infrastructure.security.repository.SecurityUserRepository
import com.example.backend.infrastructure.security.repository.SecurityUser
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mockito
import org.mockito.ArgumentMatchers.anyString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.core.userdetails.User

/**
 * UserDetailsServiceImplの単体テスト
 * 
 * Spring Securityによるユーザー詳細取得サービスをテストします。
 * 各テストケースでは、テストの意図に特化したデータを個別定義し、
 * テスト実行時にも何をテストしているかが明確になるよう設計しています。
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("ユーザー詳細サービス")
class UserDetailsServiceImplTest {

    @Mock
    private lateinit var securityUserRepository: SecurityUserRepository

    @InjectMocks
    private lateinit var userDetailsService: UserDetailsServiceImpl

    @Test
    @DisplayName("正常系: 有効なユーザーIDでUserDetailsが正常に取得される")
    fun `should load user details successfully when user exists`() {
        // Given
        val existingSecurityUser = SecurityUser(
            userId = "test_user_001",
            email = "test@example.com",
            password = "hashed_password_123"
        )

        Mockito.`when`(securityUserRepository.findByUserId(anyString())).thenReturn(existingSecurityUser)

        // When
        val userDetails = userDetailsService.loadUserByUsername("test_user_001")

        // Then
        assertThat(userDetails).isInstanceOf(User::class.java)
        assertThat(userDetails.username).isEqualTo("test_user_001")
    }

    @Test
    @DisplayName("異常系: 存在しないユーザーIDでUsernameNotFoundExceptionが発生する")
    fun `should throw UsernameNotFoundException when user does not exist`() {
        // Given
        Mockito.`when`(securityUserRepository.findByUserId(anyString())).thenReturn(null)

        // When & Then
        assertThrows<UsernameNotFoundException> {
            userDetailsService.loadUserByUsername("nonexistent_user")
        }
    }
}