@Suppress("DSL_SCOPE_VIOLATION")
plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
   alias(libs.plugins.kotlinBinaryCompatibilityValidator)
}

kotlin {
   sourceSets {
      jvmMain {
         dependencies {
            api(projects.kotestCommon)
            api(projects.kotestFramework.kotestFrameworkApi)
            api(projects.kotestAssertions.kotestAssertionsShared)
            api(projects.kotestFramework.kotestFrameworkEngine)
            api(projects.kotestFramework.kotestFrameworkDiscovery)
            api(libs.junit4)
            api(libs.kotlinx.coroutines.core)
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(libs.junit.platform.testkit)
         }
      }
   }
}
