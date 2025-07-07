plugins {
   id("kotest-multiplatform-library-conventions")
   id("kotest-android-native-conventions")
   id("kotest-watchos-device-conventions")
}

kotlin {
   sourceSets {

      val commonMain by getting {
         dependencies {
            // this is api because we want to expose `shouldBe` etc
            api(projects.kotestAssertions.kotestAssertionsShared)

            implementation(kotlin("reflect", libs.versions.kotlin.get()))
            implementation(projects.kotestCommon)
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(libs.kotlinx.coroutines.jdk8)
         }
      }

      val jvmTest by getting {
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
