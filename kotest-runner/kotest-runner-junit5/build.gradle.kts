plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {

   sourceSets {

      commonMain {
         dependencies {
            implementation(libs.kotlin.reflect)
         }
      }

      jvmMain {
         dependencies {
            api(projects.kotestCommon)
            api(projects.kotestFramework.kotestFrameworkEngine)
            api(projects.kotestAssertions.kotestAssertionsCore)
            api(projects.kotestExtensions)
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
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(libs.junit.platform.testkit)
            implementation(libs.mockk)
            implementation("dev.gradleplugins:gradle-api:8.4")
         }
      }

   }
}
