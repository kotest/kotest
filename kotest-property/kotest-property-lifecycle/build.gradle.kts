plugins {
   id("kotest-jvm-conventions")
   id("kotest-js-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {

      commonMain {
         dependencies {
            api(projects.kotestProperty)
            api(projects.kotestFramework.kotestFrameworkApi)
            implementation(libs.kotlinx.coroutines.core)
            implementation(projects.kotestCommon)
         }
      }

      commonTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }
   }
}
