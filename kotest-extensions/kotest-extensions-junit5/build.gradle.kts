plugins {
   id("java")
   kotlin("multiplatform")
   id("java-library")

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
            implementation(Libs.Coroutines.coreJvm)
            implementation(Libs.JUnitJupiter.api)
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
