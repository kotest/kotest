plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {

   sourceSets {

      val jvmMain by getting {
         dependencies {
            implementation(kotlin("reflect"))
            implementation(project(Projects.Framework.api))
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Common))
            implementation(libs.apache.commons.io)
            implementation(libs.mockk)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.Assertions.Core))
            implementation(libs.kotlinx.coroutines.core)
         }
      }
   }
}
