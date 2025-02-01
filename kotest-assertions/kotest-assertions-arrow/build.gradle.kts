plugins {
   id("kotest-multiplatform-library-conventions")
}

kotlin {
   sourceSets {

      commonMain {
         dependencies {
            api(projects.kotestAssertions.kotestAssertionsShared)
            api(libs.arrow.core)
         }
      }

      commonTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestProperty)
            implementation(libs.arrow.core)
         }
      }

      jsMain {
         dependencies {
            api(libs.arrow.core)
         }
      }

      nativeMain {
         dependencies {
            implementation(libs.arrow.core)
         }
      }
   }
}
