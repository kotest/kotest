plugins {
   id("kotest-multiplatform-library-conventions")
}

kotlin {
   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(kotlin("reflect"))
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }
   }
}
