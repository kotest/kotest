@Suppress("DSL_SCOPE_VIOLATION")
plugins {
   id("kotest-multiplatform-library-conventions")
   alias(libs.plugins.kotlinBinaryCompatibilityValidator)
}

kotlin {
   sourceSets {

      commonMain {
         dependencies {
            implementation(kotlin("reflect"))
            api(projects.kotestCommon)
            api(projects.kotestAssertions.kotestAssertionsShared)
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      jvmMain {
         dependencies {
            implementation(libs.diffutils)
            api(libs.rgxgen)
            implementation(kotlin("reflect"))
         }
      }

      commonTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }

   }
}
