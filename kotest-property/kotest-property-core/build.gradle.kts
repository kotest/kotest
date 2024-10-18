plugins {
   id("kotest-multiplatform-library-conventions")
   id("kotest-android-native-conventions")
   id("kotest-watchos-device-conventions")
   id("kotest-native-conventions")
}

kotlin {
   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(kotlin("reflect"))
            api(projects.kotestCommon)
            api(projects.kotestAssertions.kotestAssertionsShared)
            api(projects.kotestProperty)
            implementation(libs.kotlinx.coroutines.core)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }

   }
}
