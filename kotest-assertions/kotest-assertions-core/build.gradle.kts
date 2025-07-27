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
            // this is api because we want to expose `shouldBe` etc
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
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestProperty)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.opentest4j)
            implementation(libs.apache.commons.lang)
            implementation(libs.mockk)
         }
      }
   }
}
