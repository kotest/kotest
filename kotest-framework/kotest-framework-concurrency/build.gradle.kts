plugins {
   id("kotest-multiplatform-library-conventions")
}

kotlin {
   sourceSets {

      commonMain {
         dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(projects.kotestCommon)
            implementation(projects.kotestFramework.kotestFrameworkApi)
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }
   }
}
