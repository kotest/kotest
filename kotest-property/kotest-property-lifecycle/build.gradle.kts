plugins {
   id("java")
   kotlin("multiplatform")
   id("java-library")

}

kotlin {

   targets {
      jvm()
      js {
         browser()
         nodejs()
      }
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            compileOnly(kotlin("stdlib"))
            api(project(Projects.Property))
            api(project(Projects.Framework.api))
            implementation(Libs.Coroutines.coreCommon)
            implementation(project(Projects.Common))
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Assertions.Core))
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.JunitRunner))
         }
      }

      all {
         languageSettings.optIn("kotlin.time.ExperimentalTime")
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}

apply(from = "../../publish-mpp.gradle.kts")
