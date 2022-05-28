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
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Assertions.Core))
            // we use the internals of the JVM project in the tests
            implementation(project(Projects.JunitRunner))
            // this is here to test that the intellij marker 'dummy' test doesn't appear in intellij
            implementation(libs.junit.jupiter.engine)
         }
      }

      all {
         languageSettings.optIn("kotlin.time.ExperimentalTime")
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}
