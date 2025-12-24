plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {

      val jvmMain by getting {
         dependencies {
            implementation(projects.kotestCommon)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.junit.jupiter5.api)
         }
      }
   }
}
