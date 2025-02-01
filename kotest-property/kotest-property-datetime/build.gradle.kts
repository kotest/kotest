plugins {
   id("kotest-multiplatform-library-conventions")
   id("kotest-android-native-conventions")
}

kotlin {
   sourceSets {
      val commonMain by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(libs.kotlinx.datetime)
         }
      }
      val jvmTest by getting {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
         }
      }
   }
}
