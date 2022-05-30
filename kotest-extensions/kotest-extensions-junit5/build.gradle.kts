plugins {
   `java-library`
   kotlin("multiplatform")
}

kotlin {
   targets {
      jvm()
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            compileOnly(kotlin("stdlib"))
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(project(Projects.Common))
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Framework.api))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.junit.jupiter.api)
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.JunitRunner))
         }
      }

      all {
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}

apply(from = "../../publish-mpp.gradle.kts")
