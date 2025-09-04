package com.example.backend.domain.model

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import java.time.Instant

class InvalidatedTokenTest {

    @Test
    fun `正常な無効化トークンが作成される`() {
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test"
        val expiryDate = Instant.parse("2024-12-31T23:59:59Z")
        
        val invalidatedToken = InvalidatedToken(
            token = token,
            expiryDate = expiryDate
        )

        assertThat(invalidatedToken.token).isEqualTo(token)
        assertThat(invalidatedToken.expiryDate).isEqualTo(expiryDate)
    }

    @Test
    fun `異なる有効期限のトークンが作成される`() {
        val token1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test1"
        val expiryDate1 = Instant.parse("2024-06-30T23:59:59Z")
        
        val token2 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test2"
        val expiryDate2 = Instant.parse("2024-12-31T23:59:59Z")

        val invalidatedToken1 = InvalidatedToken(token1, expiryDate1)
        val invalidatedToken2 = InvalidatedToken(token2, expiryDate2)

        assertThat(invalidatedToken1.expiryDate).isBefore(invalidatedToken2.expiryDate)
        assertThat(invalidatedToken1.token).isNotEqualTo(invalidatedToken2.token)
    }

    @Test
    fun `トークンが等価性を持つ`() {
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test"
        val expiryDate = Instant.parse("2024-12-31T23:59:59Z")
        
        val invalidatedToken1 = InvalidatedToken(token, expiryDate)
        val invalidatedToken2 = InvalidatedToken(token, expiryDate)

        assertThat(invalidatedToken1).isEqualTo(invalidatedToken2)
        assertThat(invalidatedToken1.hashCode()).isEqualTo(invalidatedToken2.hashCode())
    }
} 