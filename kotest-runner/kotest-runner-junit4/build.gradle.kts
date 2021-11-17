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
         }
      }

      val jvmMain by getting {
         dependencies {
            api(project(Projects.Common))
            api(project(Projects.Framework.api))
            api(project(Projects.Assertions.Shared))
            api(project(Projects.Framework.engine))
            api(project(Projects.Extensions))
            api(Testing.junit4)
            api(Libs.Coroutines.coreJvm)
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.Assertions.Core))
            implementation(Libs.JUnitPlatform.testkit)
         }
      }

      all {
         languageSettings.optIn("kotlin.time.ExperimentalTime")
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}

apply(from = "../../publish-mpp.gradle.kts")

