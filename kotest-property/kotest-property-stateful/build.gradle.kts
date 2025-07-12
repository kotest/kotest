plugins {
   id("kotest-multiplatform-library-conventions")
}

kotlin {
   sourceSets {

      commonMain {
         dependencies {
         }
      }

      jvmMain {
         dependencies {
            implementation("org.springframework:spring-jdbc:6.2.8")
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestExtensions.kotestExtensionsTestcontainers)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(libs.testcontainers.mysql)
            implementation(libs.mysql.driver)
         }
      }
   }
}
