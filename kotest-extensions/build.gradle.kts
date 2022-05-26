plugins {
   `java-library`
   kotlin("multiplatform")
}

kotlin {

   targets {
      jvm()
   }

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
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.JunitRunner))
            implementation(project(Projects.Assertions.Core))
            implementation(libs.kotlinx.coroutines.core)
         }
      }
   }
}

apply(from = "../publish-mpp.gradle.kts")
