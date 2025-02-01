plugins {
   id("kotest-multiplatform-library-conventions")
}

kotlin {
   sourceSets {

      commonMain {
         dependencies {
            api(projects.kotestAssertions.kotestAssertionsShared)
            api(projects.kotestProperty)
            api(libs.arrow.optics)
            implementation(libs.arrow.functions)
         }
      }

      commonTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsArrow)
            implementation(libs.arrow.core)

         }
      }

      jsMain {
         dependencies {
            api(libs.arrow.optics)
         }
      }

      nativeMain {
         dependencies {
            implementation(libs.arrow.optics)
         }
      }
   }
}
