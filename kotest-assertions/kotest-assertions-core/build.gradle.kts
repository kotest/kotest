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
            // required for the base matcher interface
            api(projects.kotestAssertions.kotestAssertionsShared)

            implementation(libs.kotlin.reflect)
            implementation(projects.kotestCommon)
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      commonTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }

      jvmMain {
         dependencies {
            implementation(libs.kotlinx.coroutines.jdk8)
            implementation(libs.diffutils) // used for diffing large strings in assertions
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestProperty)
            implementation(projects.kotestAssertions.kotestAssertionsTable)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.apache.commons.lang)
            implementation(libs.mockk)
            implementation(libs.jimfs)
         }
      }
   }
}
