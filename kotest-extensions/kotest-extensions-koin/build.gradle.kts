plugins {
   id("kotest-jvm-conventions")
   id("kotest-js-not-wasm-conventions")
   id("kotest-native-conventions")
   id("kotest-publishing-conventions")
}

kotlin {

   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(libs.koin.core)
            implementation(libs.koin.test.get().let { "${it.module}:${it.versionConstraint.requiredVersion}" }) {
               exclude(group = "junit", module = "junit")
            }
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(projects.kotestRunner.kotestRunnerJunit5)
            implementation(libs.mockk)
         }
      }
   }
}
