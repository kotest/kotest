@Suppress("DSL_SCOPE_VIOLATION")
plugins {
   id("kotest-multiplatform-library-conventions")
   alias(libs.plugins.kotlinBinaryCompatibilityValidator)
}

kotlin {
   sourceSets {

      commonMain {
         dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(projects.kotestCommon)
            implementation(projects.kotestFramework.kotestFrameworkApi)
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }
   }
}
