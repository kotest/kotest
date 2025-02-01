plugins {
   id("kotest-multiplatform-library-conventions")
   id("kotest-android-native-conventions")
}

kotlin {
   sourceSets {
      val commonMain by getting {
         dependencies {
            implementation(projects.kotestProperty)
            implementation(libs.kotlinx.datetime)
         }
      }
      val commonTest by getting {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
         }
      }
      val jvmTest by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }
   }
}
