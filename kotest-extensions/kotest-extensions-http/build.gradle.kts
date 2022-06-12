plugins {
   id("kotest-jvm-conventions")
   id("kotest-js-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(libs.ktor.client.apache)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.Assertions.Core))
            implementation(libs.mockserver.netty)
            implementation(libs.kotest.extensions.mockserver)
         }
      }

      if (!project.hasProperty(Ci.JVM_ONLY)) {
         val jsMain by getting {
            dependencies {
               implementation(libs.ktor.client.js)
            }
         }
      }
   }
}
