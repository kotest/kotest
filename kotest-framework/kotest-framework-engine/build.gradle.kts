plugins {
   id("kotest-multiplatform-library-conventions")
}

kotlin {
   sourceSets {

      val commonMain by getting {
         dependencies {
            api(project(Projects.Assertions.Shared))
            implementation(kotlin("reflect"))
            api(project(Projects.Common)) // needs to be API so the domain objects are open

            // this is API because we want people to be able to use the functionality in their tests
            // without needing to declare this dependency as well
            api(project(Projects.Framework.api))

            // used to install the debug probes for coroutines
            implementation(libs.kotlinx.coroutines.debug)
            implementation(libs.kotlinx.coroutines.core)
            // used for the test scheduler
            implementation(libs.kotlinx.coroutines.test)
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(libs.kotlinx.coroutines.test)

            api(libs.classgraph)

            // needed to scan for spec classes
            api(project(Projects.Discovery))

            // we use AssertionFailedError from opentest4j
            implementation(libs.opentest4j)

            // used to write to the console with fancy colours!
            api(libs.mordant)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(kotlin("stdlib"))
            implementation(project(Projects.Assertions.Core))
            implementation(project(Projects.Framework.datatest))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.mockk)
         }
      }
   }
}
