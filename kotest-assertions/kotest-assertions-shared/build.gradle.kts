plugins {
   id("kotest-multiplatform-library-conventions")
   id("kotest-android-native-conventions")
}

kotlin {

   sourceSets {

      val commonMain by getting {
         dependencies {
            // this is api because we want to expose `shouldBe` etc
            api(projects.kotestAssertions.kotestAssertionsApi)

            implementation(kotlin("reflect"))
            implementation(projects.kotestCommon)
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(libs.kotlinx.coroutines.jdk8)
            implementation(libs.diffutils)
            implementation(libs.opentest4j)
         }
      }
   }
}
