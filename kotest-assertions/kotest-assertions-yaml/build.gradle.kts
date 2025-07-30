plugins {
   id("kotest-multiplatform-library-conventions")
   id("kotest-publishing-conventions")
   alias(libs.plugins.kotlin.serialization)
}

kotlin {

   sourceSets {
      commonMain {
         dependencies {
            implementation(projects.kotestCommon)
            implementation(libs.kaml)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }

      commonTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestProperty)
         }
      }
   }
}
