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
            api(libs.junit.platform5.api)
            api(libs.junit.platform5.launcher)
            api(libs.junit.platform5.engine)
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(libs.junit.platform5.testkit)
            implementation(libs.mockk)
            implementation("dev.gradleplugins:gradle-api:8.4")
         }
      }

   }
}
