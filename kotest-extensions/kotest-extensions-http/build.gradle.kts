plugins {
   id("kotest-jvm-conventions")
   id("kotest-js-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {

      val commonMain by getting {
         dependencies {
            compileOnly(kotlin("stdlib"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
         }
      }

      val jsMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(libs.ktor.client.js)
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(libs.ktor.client.apache)
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.JunitRunner))
            implementation(project(Projects.Assertions.Core))
            implementation(libs.mockserver.netty)
            implementation(libs.kotest.extensions.mockserver)
         }
      }

      all {
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}
