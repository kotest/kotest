plugins {
   id("kotest-multiplatform-library-conventions")
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
