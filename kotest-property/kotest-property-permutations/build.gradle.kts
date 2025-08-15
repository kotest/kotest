plugins {
   id("kotest-jvm-conventions")
   id("kotest-js-conventions")
   id("kotest-android-native-conventions")
   id("kotest-watchos-device-conventions")
   id("kotest-native-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {

      commonMain {
         dependencies {
            implementation(libs.kotlin.reflect)
            api(projects.kotestCommon)
            api(projects.kotestAssertions.kotestAssertionsShared)
            api(projects.kotestProperty)
            implementation(libs.kotlinx.coroutines.core)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }

   }
}
