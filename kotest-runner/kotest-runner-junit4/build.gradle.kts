plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      val jvmMain by getting {
         dependencies {
            api(project(Projects.Common))
            api(project(Projects.Framework.api))
            api(project(Projects.Assertions.Shared))
            api(project(Projects.Framework.engine))
            api(project(Projects.Extensions))
            api(libs.junit4)
            api(libs.kotlinx.coroutines.core)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.Assertions.Core))
            implementation(libs.junit.platform.testkit)
         }
      }
   }
}
