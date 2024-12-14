plugins {
   id("kotest-js-not-wasm-conventions")
   id("kotest-jvm-conventions")
}

kotlin {

   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsShared)
            implementation(libs.ktor.client.core)
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(libs.ktor.server.core)
            implementation(libs.ktor.server.testHost)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(projects.kotestRunner.kotestRunnerJunit5)
         }
      }
   }
}
