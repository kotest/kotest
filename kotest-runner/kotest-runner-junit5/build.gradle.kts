plugins {
   id("java")
   kotlin("multiplatform")
   id("java-library")
   id("maven-publish")
   id("com.adarshr.test-logger")
}

kotlin {

   targets {
      jvm {
         compilations.all {
            kotlinOptions {
               jvmTarget = "1.8"
            }
         }
      }
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            compileOnly(kotlin("stdlib"))
            implementation(kotlin("reflect"))
         }
      }

      val jvmMain by getting {
         dependencies {
//            implementation(Libs.Kotlin.kotlinScriptRuntime)
            api(project(Projects.Framework.api))
            api(project(Projects.Common))
            api(project(Projects.Framework.engine))
            api(project(Projects.Discovery))
            api(project(Projects.Assertions.Core))
            api(project(Projects.Extensions))
            api(project(Projects.Framework.concurrency))
            api(Libs.Coroutines.coreJvm)
            api(Libs.JUnitPlatform.engine)
            api(Libs.JUnitPlatform.api)
            api(Libs.JUnitPlatform.launcher)
            api(Libs.JUnitJupiter.api)
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.JunitRunner))
            implementation(project(Projects.Assertions.Core))
            implementation(Libs.JUnitPlatform.testkit)
            implementation(Libs.Mocking.mockk)
         }
      }

      all {
         languageSettings.optIn("kotlin.time.ExperimentalTime")
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}

apply(from = "../../publish-mpp.gradle.kts")

