plugins {
   id("kotest-jvm-conventions")
   id("kotest-js-conventions")
   id("kotest-publishing-conventions")
   id("linux-only-tests-conventions")
}

kotlin {
   sourceSets {

      val commonMain by getting {
         dependencies {
            api(projects.kotestProperty)
            api(projects.kotestFramework.kotestFrameworkEngine)
            implementation(libs.kotlinx.coroutines.core)
            implementation(projects.kotestCommon)
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }
   }
}
