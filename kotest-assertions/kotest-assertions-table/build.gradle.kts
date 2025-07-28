plugins {
   id("kotest-jvm-conventions")
   id("kotest-js-wasm-conventions")
   id("kotest-native-conventions")
   id("kotest-android-native-conventions")
   id("kotest-watchos-device-conventions")
   id("kotest-publishing-conventions")
}

kotlin {

   sourceSets {

      commonMain {
         dependencies {
            implementation(libs.kotlin.reflect)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }

      commonTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }

      jvmMain {
         dependencies {
            implementation(libs.apache.commons.lang)
         }
      }
   }
}
