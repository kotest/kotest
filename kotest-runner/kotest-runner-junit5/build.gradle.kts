plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {

   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(kotlin("reflect"))
         }
      }

      val jvmMain by getting {
         dependencies {
            api(project(Projects.Framework.api))
            api(project(Projects.Common))
            api(project(Projects.Framework.engine))
            api(project(Projects.Discovery))
            api(project(Projects.Assertions.Core))
            api(project(Projects.Extensions))
            api(project(Projects.Framework.concurrency))
            api(libs.kotlinx.coroutines.core)
            api(libs.junit.platform.engine)
            api(libs.junit.platform.api)
            api(libs.junit.platform.launcher)
            api(libs.junit.jupiter.api)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.JunitRunner))
            implementation(project(Projects.Assertions.Core))
            implementation(libs.junit.platform.testkit)
            implementation(libs.mockk)
         }
      }

   }
}
