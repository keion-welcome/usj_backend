package com.example.backend.domain.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.assertj.core.api.Assertions.assertThat

class UserTest {

    @Test
    fun `正常なユーザーが作成される`() {
        val user = User(
            id = 1L,
            userId = "550e8400-e29b-41d4-a716-446655440001",
            username = "testuser",
            email = "test@example.com",
            password = "hashedPassword"
        )

        assertThat(user.id).isEqualTo(1L)
        assertThat(user.userId).isEqualTo("550e8400-e29b-41d4-a716-446655440001")
        assertThat(user.username).isEqualTo("testuser")
        assertThat(user.email).isEqualTo("test@example.com")
        assertThat(user.password).isEqualTo("hashedPassword")
    }

    @Test
    fun `デフォルト値でユーザーが作成される`() {
        val user = User(
            username = "testuser",
            email = "test@example.com",
            password = "hashedPassword"
        )

        assertThat(user.id).isNull()
        assertThat(user.userId).isNull()
        assertThat(user.username).isEqualTo("testuser")
        assertThat(user.email).isEqualTo("test@example.com")
        assertThat(user.password).isEqualTo("hashedPassword")
    }

    @Test
    fun `ユーザーが等価性を持つ`() {
        val user1 = User(
            id = 1L,
            userId = "550e8400-e29b-41d4-a716-446655440001",
            username = "testuser",
            email = "test@example.com",
            password = "hashedPassword"
        )

        val user2 = User(
            id = 1L,
            userId = "550e8400-e29b-41d4-a716-446655440001",
            username = "testuser",
            email = "test@example.com",
            password = "hashedPassword"
        )

        assertThat(user1).isEqualTo(user2)
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode())
    }
} 