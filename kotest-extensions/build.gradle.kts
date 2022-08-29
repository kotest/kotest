plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {

   sourceSets {

      val jvmMain by getting {
         dependencies {
            implementation(kotlin("reflect"))
            implementation(projects.kotestFramework.kotestFrameworkApi)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestCommon)
            implementation(libs.apache.commons.io)
            implementation(libs.mockk)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(libs.kotlinx.coroutines.core)
         }
      }
   }
}
