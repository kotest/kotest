@Suppress("DSL_SCOPE_VIOLATION")
plugins {
   id("kotest-multiplatform-library-conventions")
   alias(libs.plugins.kotlin.serialization)
   alias(libs.plugins.kotlinBinaryCompatibilityValidator)
}

kotlin {

   sourceSets {

      commonMain {
         dependencies {
            implementation(projects.kotestCommon)
            implementation(libs.kotlinx.serialization.json)
            implementation(projects.kotestAssertions.kotestAssertionsShared)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }

      commonTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkApi)
            implementation(projects.kotestFramework.kotestFrameworkDatatest)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestProperty)
         }
      }

      jvmMain {
         dependencies {
            implementation(libs.jayway.json.path)
         }
      }
   }
}
