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
            implementation(kotlin("reflect", null))
            api(projects.kotestCommon)
            api(projects.kotestAssertions.kotestAssertionsShared)
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(libs.diffutils)
            api(libs.rgxgen)
            implementation(kotlin("reflect", null))
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(kotlin("test-common", null))
            implementation(kotlin("test-annotations-common", null))
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }

   }
}
