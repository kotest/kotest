plugins {
   java
   `java-library`
   `maven-publish`
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
            implementation(kotlin("reflect"))
         }
      }

      val jvmMain by getting {
         dependencies {
//            implementation(Libs.Kotlin.kotlinScriptRuntime)
            api(project(Projects.Framework.api))
            api(project(Projects.Common))
            api(project(Projects.Framework.engine))
            api(project(Projects.Discovery))
            api(project(Projects.Assertions.Core))
            api(project(Projects.Extensions))
            api(project(Projects.Framework.concurrency))
            api(libs.kotlinx.coroutines.core)
            api(libs.junit.platform.engine)
            api(libs.junit.platform.api)
            api(libs.junit.platform.launcher)
            api(libs.junit.jupiter.api)
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.JunitRunner))
            implementation(project(Projects.Assertions.Core))
            implementation(libs.junit.platform.testkit)
            implementation(libs.mockk)
         }
      }

      all {
         languageSettings.optIn("kotlin.time.ExperimentalTime")
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}

apply(from = "../../publish-mpp.gradle.kts")

