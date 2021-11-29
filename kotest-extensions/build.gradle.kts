plugins {
   id("java")
   kotlin("multiplatform")
   id("java-library")
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
            implementation(Libs.Apache.commonsio)
            implementation(Libs.Mocking.mockk)
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.JunitRunner))
            implementation(project(Projects.Assertions.Core))
            implementation(Libs.Coroutines.coreJvm)
         }
      }
   }
}

apply(from = "../publish-mpp.gradle.kts")
