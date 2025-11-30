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
            api(projects.kotestRunner.kotestRunnerJunitPlatform)
            api(libs.kotlinx.coroutines.core)
            api(libs.junit.platform6.engine)
            api(libs.junit.platform6.api)
            api(libs.junit.platform6.launcher)
            api(libs.junit.jupiter6.api)
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(libs.junit.platform6.testkit)
            implementation(libs.mockk)
            implementation("dev.gradleplugins:gradle-api:8.4")
         }
      }

   }
}
