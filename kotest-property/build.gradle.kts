plugins {
   id("kotest-multiplatform-library-conventions")
}

kotlin {
   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(kotlin("reflect"))
            api(projects.kotestCommon)
            api(projects.kotestAssertions.kotestAssertionsShared)
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(libs.diffutils)
            api(libs.rgxgen)
            implementation(kotlin("reflect"))
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }

   }
}
