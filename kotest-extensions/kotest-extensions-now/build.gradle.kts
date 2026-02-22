plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      jvmMain {
         dependencies {
            implementation(libs.kotlin.reflect)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(libs.mockk)
         }
      }
      jvmTest {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(libs.kotlinx.coroutines.core)
         }
      }
   }
}
