@Suppress("DSL_SCOPE_VIOLATION")
plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {

   sourceSets {

      commonMain {
         dependencies {
            implementation(kotlin("reflect"))
         }
      }

      jvmMain {
         dependencies {
            api(projects.kotestFramework.kotestFrameworkApi)
            api(projects.kotestCommon)
            api(projects.kotestFramework.kotestFrameworkEngine)
            api(projects.kotestFramework.kotestFrameworkDiscovery)
            api(projects.kotestAssertions.kotestAssertionsCore)
            api(projects.kotestExtensions)
            api(projects.kotestFramework.kotestFrameworkConcurrency)
            api(libs.kotlinx.coroutines.core)
            api(libs.junit.platform.engine)
            api(libs.junit.platform.api)
            api(libs.junit.platform.launcher)
            api(libs.junit.jupiter.api)
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestRunner.kotestRunnerJunit5)
            implementation(projects.kotestFramework.kotestFrameworkDatatest)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(libs.junit.platform.testkit)
            implementation(libs.mockk)
         }
      }

   }
}
