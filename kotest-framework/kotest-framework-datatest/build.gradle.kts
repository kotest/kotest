plugins {
   id("kotest-multiplatform-library-conventions")
}

kotlin {
   sourceSets {

      val commonMain by getting {
         dependencies {
            compileOnly(kotlin("stdlib"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(project(Projects.Common))
            implementation(project(Projects.Framework.api))
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.Assertions.Core))
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.JunitRunner))
         }
      }

      all {
         languageSettings.optIn("kotlin.time.ExperimentalTime")
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}
