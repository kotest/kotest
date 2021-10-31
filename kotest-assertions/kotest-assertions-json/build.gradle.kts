plugins {
   id("java")
   kotlin("multiplatform")
   kotlin("plugin.serialization")
   id("java-library")
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
      js(IR) {
         browser()
         nodejs()
      }
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(Libs.Serialization.json)
            implementation(project(Projects.Assertions.Shared))
            implementation(Libs.Jayway.jsonpath)
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(project(Projects.Assertions.Core))
            implementation(project(Projects.Framework.api))
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Property))
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(Libs.Jayway.jsonpath)
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.JunitRunner))
         }
      }

      all {
         languageSettings.optIn("kotlin.time.ExperimentalTime")
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
         languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
      }
   }
}

apply(from = "../../publish-mpp.gradle.kts")
