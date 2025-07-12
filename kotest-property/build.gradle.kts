plugins {
   id("kotest-multiplatform-library-conventions")
   id("kotest-android-native-conventions")
   id("kotest-watchos-device-conventions")
   id("kotest-native-conventions")
   id("kotest-js-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(libs.kotlin.reflect)
            api(projects.kotestCommon)
            api(projects.kotestAssertions.kotestAssertionsShared)
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(libs.diffutils)
            api(libs.rgxgen)
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }

   }
}
