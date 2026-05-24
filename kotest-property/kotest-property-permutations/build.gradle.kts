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
            api(projects.kotestCommon)
            api(projects.kotestAssertions.kotestAssertionsShared)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(libs.kotlin.reflect)
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }
   }
}
