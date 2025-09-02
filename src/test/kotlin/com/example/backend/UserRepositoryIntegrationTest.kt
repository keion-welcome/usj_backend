package com.example.backend

import com.github.database.rider.core.api.dataset.DataSet
import com.github.database.rider.junit5.DBUnitExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.assertj.core.api.Assertions.assertThat

@ExtendWith(DBUnitExtension::class)
@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
])
@Transactional
class UserRepositoryIntegrationTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Test
    fun `H2データベースが正常に起動する`() {
        // H2データベースが正常に起動していることを確認
        val result = entityManager.entityManager
            .createNativeQuery("SELECT 1")
            .singleResult
        
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun `H2データベースのバージョンが取得できる`() {
        // H2データベースのバージョン情報が取得できることを確認
        val version = entityManager.entityManager
            .createNativeQuery("SELECT H2VERSION()")
            .singleResult as String
        
        assertThat(version).isNotEmpty()
        assertThat(version).contains(".")
    }

    @Test
    fun `H2データベースで基本的な計算ができる`() {
        // H2データベースで基本的な計算ができることを確認
        val result = entityManager.entityManager
            .createNativeQuery("SELECT 2 + 3")
            .singleResult
        
        assertThat(result).isEqualTo(5)
    }
}