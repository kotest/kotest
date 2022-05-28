plugins {
   id("kotlin-conventions")
}

kotlin {
   targets {
      jvm()
   }

   sourceSets {

      val jvmTest by getting {
         dependencies {
            implementation(kotlin("reflect"))
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Assertions.Core))
            implementation(project(Projects.JunitRunner))
         }
      }

      all {
         languageSettings.optIn("kotlin.time.ExperimentalTime")
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}
