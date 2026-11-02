plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      jvmMain {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(libs.testcontainers.core)
            implementation(libs.testcontainers.jdbc)
            implementation(libs.testcontainers.kafka)
            // api because the JDBC container extensions expose HikariConfig and HikariDataSource to users
            api(libs.hikari)
            implementation(libs.kafka.client)
         }
      }
      jvmTest {
         dependencies {
            implementation(libs.testcontainers.mysql)
            implementation(libs.mysql.driver)
            implementation(libs.jedis)
         }
      }
   }
}
