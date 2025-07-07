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
            implementation(libs.testcontainers.elastic)
            implementation(libs.hikari)
            implementation(libs.kafka.client)
         }
      }
      jvmTest {
         dependencies {
            implementation(libs.testcontainers.mysql)
            implementation(libs.mysql.driver)
         }
      }
   }
}
