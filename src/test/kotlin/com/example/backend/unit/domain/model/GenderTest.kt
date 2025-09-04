package com.example.backend.domain.model

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

class GenderTest {

    @Test
    fun `全ての性別の値が正しく定義されている`() {
        val genders = Gender.values()
        
        assertThat(genders).hasSize(4)
        assertThat(genders.map { it.name }).containsExactlyInAnyOrder(
            "MALE",
            "FEMALE", 
            "OTHER",
            "PREFER_NOT_TO_SAY"
        )
    }

    @Test
    fun `性別の値が正しく取得できる`() {
        assertThat(Gender.MALE.name).isEqualTo("MALE")
        assertThat(Gender.FEMALE.name).isEqualTo("FEMALE")
        assertThat(Gender.OTHER.name).isEqualTo("OTHER")
        assertThat(Gender.PREFER_NOT_TO_SAY.name).isEqualTo("PREFER_NOT_TO_SAY")
    }

    @Test
    fun `性別の値が一意である`() {
        val genderSet = Gender.values().toSet()
        assertThat(genderSet).hasSize(4)
    }
} 