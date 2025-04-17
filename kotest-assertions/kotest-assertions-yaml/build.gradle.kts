plugins {
   id("kotest-multiplatform-library-conventions")
   id("kotest-publishing-conventions")
   alias(libs.plugins.kotlin.serialization)
}

kotlin {

   sourceSets {
      val commonMain by getting {
         dependencies {
            implementation(projects.kotestCommon)
            implementation(libs.kaml)
            implementation(projects.kotestAssertions.kotestAssertionsShared)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestProperty)
         }
      }
   }
}
