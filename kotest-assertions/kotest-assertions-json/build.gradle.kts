plugins {
   id("kotest-multiplatform-library-conventions")
   alias(libs.plugins.kotlin.serialization)
}

kotlin {

   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(projects.kotestCommon)
            implementation(libs.kotlinx.serialization.json)
            implementation(projects.kotestAssertions.kotestAssertionsShared)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkApi)
            implementation(projects.kotestFramework.kotestFrameworkDatatest)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestProperty)
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(libs.jayway.json.path)
         }
      }
   }
}
