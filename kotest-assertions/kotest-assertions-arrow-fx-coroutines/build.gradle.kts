plugins {
   id("kotest-multiplatform-library-conventions")
}

kotlin {
   sourceSets {
      commonMain {
         dependencies {
            api(projects.kotestAssertions.kotestAssertionsShared)
            api(projects.kotestAssertions.kotestAssertionsArrow)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            api(libs.arrow.fx.coroutines)
         }
      }

      commonTest {
         dependencies {
            implementation(projects.kotestProperty)
            implementation(libs.arrow.fx.coroutines)
         }
      }

      jsMain {
         dependencies {
            api(libs.arrow.fx.coroutines)
         }
      }

      nativeMain {
         dependencies {
            implementation(libs.arrow.fx.coroutines)
         }
      }
   }
}
