package com.example.backend.domain.model

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDate

class ProfileTest {

    @Test
    fun `正常なプロフィールが作成される`() {
        val profile = Profile(
            id = 1L,
            userId = "test-user-id",
            nickname = "テストユーザー",
            gender = Gender.MALE,
            birthdate = LocalDate.of(1990, 1, 1),
            area = "東京都",
            occupation = "大学生",
            hasAnnualPass = true
        )

        assertThat(profile.id).isEqualTo(1L)
        assertThat(profile.userId).isEqualTo(1L)
        assertThat(profile.nickname).isEqualTo("テストユーザー")
        assertThat(profile.gender).isEqualTo(Gender.MALE)
        assertThat(profile.birthdate).isEqualTo(LocalDate.of(1990, 1, 1))
        assertThat(profile.area).isEqualTo("東京都")
        assertThat(profile.occupation).isEqualTo("大学生")
        assertThat(profile.hasAnnualPass).isTrue()
    }

    @Test
    fun `デフォルト値でプロフィールが作成される`() {
        val profile = Profile(
            nickname = "テストユーザー",
            gender = Gender.FEMALE,
            birthdate = LocalDate.of(1995, 5, 15),
            area = "大阪府",
            occupation = "会社員",
            hasAnnualPass = false
        )

        assertThat(profile.id).isNull()
        assertThat(profile.userId).isNull()
        assertThat(profile.nickname).isEqualTo("テストユーザー")
        assertThat(profile.gender).isEqualTo(Gender.FEMALE)
        assertThat(profile.birthdate).isEqualTo(LocalDate.of(1995, 5, 15))
        assertThat(profile.area).isEqualTo("大阪府")
        assertThat(profile.occupation).isEqualTo("会社員")
        assertThat(profile.hasAnnualPass).isFalse()
    }

    @Test
    fun `異なる性別のプロフィールが作成される`() {
        val maleProfile = Profile(
            nickname = "男性ユーザー",
            gender = Gender.MALE,
            birthdate = LocalDate.of(1990, 1, 1),
            area = "東京都",
            occupation = "会社員",
            hasAnnualPass = true
        )

        val femaleProfile = Profile(
            nickname = "女性ユーザー",
            gender = Gender.FEMALE,
            birthdate = LocalDate.of(1995, 5, 15),
            area = "大阪府",
            occupation = "学生",
            hasAnnualPass = false
        )

        val otherProfile = Profile(
            nickname = "その他ユーザー",
            gender = Gender.OTHER,
            birthdate = LocalDate.of(1985, 10, 20),
            area = "神奈川県",
            occupation = "フリーランス",
            hasAnnualPass = true
        )

        assertThat(maleProfile.gender).isEqualTo(Gender.MALE)
        assertThat(femaleProfile.gender).isEqualTo(Gender.FEMALE)
        assertThat(otherProfile.gender).isEqualTo(Gender.OTHER)
    }
} 