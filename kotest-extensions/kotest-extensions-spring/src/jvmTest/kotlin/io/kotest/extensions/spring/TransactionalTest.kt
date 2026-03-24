package io.kotest.extensions.spring

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FreeSpec
import org.flywaydb.core.internal.jdbc.JdbcTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.PostgreSQLContainer
import io.kotest.matchers.shouldBe

@SpringBootApplication
class Application

fun main(args: Array<String>) {
   runApplication<Application>(*args)
}

@Entity(name = "kotest_user")
class KotestUserEntity(
   val username: String,
) {
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kotest_user_pk_sequence")
   @SequenceGenerator(name = "kotest_user_pk_sequence", allocationSize = 1)
   var pkId: Long? = null

}

@Repository
interface KotestUserRepository : JpaRepository<KotestUserEntity, Long>

@TestConfiguration(proxyBeanMethods = false)
class PostgresTCConfig {
   @Bean
   @ServiceConnection
   fun postgresContainer() = PostgreSQLContainer<Nothing>("postgres:16.8").apply { start() }
}

@SpringBootTest
@Transactional
@ApplyExtension(SpringExtension::class)
@Import(PostgresTCConfig::class)
class TransactionalTest(
   private val kotestUserRepository: KotestUserRepository,
   private val jdbcTemplate: JdbcTemplate,
) : FreeSpec({

   beforeEach {
      kotestUserRepository.saveAll(
         listOf(
            KotestUserEntity(username = "user1"),
            KotestUserEntity(username = "user2"),
            KotestUserEntity(username = "user3"),
         )
      )
   }
   afterEach {
      jdbcTemplate.execute("ALTER SEQUENCE kotest_user_pk_sequence RESTART WITH 1")
   }

   "on the first test, should have 3 users with correct PK IDs" {
      with(kotestUserRepository.findAll()) {
         size shouldBe 3
         // IDS: 1,2,3
         forEachIndexed { index, kotestUserEntity ->
            kotestUserEntity.pkId shouldBe index + 1L
         }
      }
   }

   // the afterEach should have reset the sequence, and given us back IDs 1,2,3
   "on the second test, should still have 3 users with the same PK IDs" {
      with(kotestUserRepository.findAll()) {
         size shouldBe 3
         forEachIndexed { index, kotestUserEntity ->
            kotestUserEntity.pkId shouldBe index + 1L
         }
      }
   }
})
