plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {

      jvmMain {
         dependencies {
            implementation(projects.kotestCommon)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestFramework.kotestFrameworkApi)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.junit.jupiter.api)
         }
      }
   }
}
