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
            implementation(kotlin("reflect"))
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Assertions.Core))
            // we use the internals of the JVM project in the tests
            implementation(project(Projects.JunitRunner))
            implementation(Libs.Coroutines.coreJvm)
            implementation(Libs.Mocking.mockk)
            implementation(Libs.JUnitPlatform.engine)
            implementation(Libs.JUnitPlatform.api)
            implementation(Libs.JUnitPlatform.launcher)
            implementation(Libs.JUnitJupiter.api)
            // this is here to test that the intellij marker 'dummy' test doesn't appear in intellij
            implementation(Libs.JUnitJupiter.engine)
         }
      }

      all {
         languageSettings.optIn("kotlin.time.ExperimentalTime")
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}

apply(from = "../../nopublish.gradle")
