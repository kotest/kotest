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
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.coroutines.test)
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
