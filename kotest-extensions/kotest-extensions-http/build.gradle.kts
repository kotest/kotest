plugins {
   id("java")
   kotlin("multiplatform")
   id("java-library")

}

kotlin {

   targets {
      jvm()
      js(BOTH) {
         browser()
         nodejs()
      }
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            compileOnly(kotlin("stdlib"))
            implementation(Libs.Coroutines.coreCommon)
            implementation(Libs.Ktor.clientCore)
         }
      }

      val jsMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(Libs.Ktor.clientJs)
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(Libs.Ktor.clientApache)
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.JunitRunner))
            implementation(project(Projects.Assertions.Core))
            implementation(Libs.MockServer.netty)
            implementation(Libs.Kotest.Extensions.MockServer)
         }
      }

      all {
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}

apply(from = "../../publish-mpp.gradle.kts")
