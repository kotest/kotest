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
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(projects.kotestRunner.kotestRunnerJunit5)
            implementation(projects.kotestProperty)
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      all {
         languageSettings.optIn("kotlin.time.ExperimentalTime")
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}
