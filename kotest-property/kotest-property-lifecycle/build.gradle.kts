plugins {
   id("kotest-jvm-conventions")
   id("kotest-js-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {

      val commonMain by getting {
         dependencies {
            api(projects.kotestProperty)
            api(projects.kotestFramework.kotestFrameworkApi)
            implementation(libs.kotlinx.coroutines.core)
            implementation(projects.kotestCommon)
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }
   }
}
