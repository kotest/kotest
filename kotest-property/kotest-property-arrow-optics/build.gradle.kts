plugins {
   id("kotest-multiplatform-library-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {

      commonMain {
         dependencies {
            api(projects.kotestAssertions.kotestAssertionsShared)
            api(projects.kotestProperty.kotestPropertyArrow)
            api(libs.arrow.optics)
            implementation(libs.arrow.functions)
         }
      }

      commonTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsArrow)
            implementation(libs.arrow.optics)
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
