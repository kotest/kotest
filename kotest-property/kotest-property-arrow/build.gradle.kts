plugins {
   id("kotest-multiplatform-library-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {

      commonMain {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            api(projects.kotestProperty)
            api(libs.arrow.optics)
            implementation(libs.arrow.functions)
         }
      }

      commonTest {
         dependencies {
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
