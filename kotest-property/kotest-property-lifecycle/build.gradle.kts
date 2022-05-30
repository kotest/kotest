plugins {
   `java-library`
   kotlin("multiplatform")
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
            compileOnly(kotlin("stdlib"))
            api(project(Projects.Property))
            api(project(Projects.Framework.api))
            implementation(libs.kotlinx.coroutines.core)
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
