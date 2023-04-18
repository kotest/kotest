plugins {
   id("kotest-multiplatform-library-conventions")
}

kotlin {
   sourceSets {

      commonMain {
         dependencies {
            implementation(kotlin("reflect"))
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      commonTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }
   }
}
