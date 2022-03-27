plugins {
   id("java")
   kotlin("multiplatform")
   kotlin("plugin.serialization")
   id("java-library")

}

kotlin {

   targets {
      jvm()
      js(IR) {
         browser()
         nodejs()
      }
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(Libs.Serialization.json)
            implementation(project(Projects.Common))
            implementation(project(Projects.Assertions.Shared))
            implementation(project(Projects.Assertions.Core))
            implementation(Libs.Jayway.jsonpath)
         }
      }

      val commonTest by getting {
         dependencies {
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
            implementation(project(Projects.Framework.datatest))
            implementation(project(Projects.JunitRunner))
         }
      }

      all {
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
         languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
         languageSettings.optIn("kotlin.RequiresOptIn")
      }
   }
}

apply(from = "../../publish-mpp.gradle.kts")
