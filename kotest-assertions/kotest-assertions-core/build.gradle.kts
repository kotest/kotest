plugins {
   id("kotest-multiplatform-library-conventions")
}

kotlin {
   sourceSets {

      val commonMain by getting {
         dependencies {
            compileOnly(kotlin("stdlib"))
            implementation(kotlin("reflect"))
            implementation(project(Projects.Common))
            implementation(project(Projects.Assertions.Api))
            implementation(libs.kotlinx.coroutines.core)
            // this is api because we want to expose `shouldBe` etc
            api(project(Projects.Assertions.Shared))
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(libs.kotlinx.coroutines.jdk8)
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.Property))
            implementation(project(Projects.JunitRunner))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.opentest4j)
            implementation(libs.apache.commons.lang)
            implementation(libs.mockk)
         }
      }

      all {
         languageSettings.optIn("kotlin.time.ExperimentalTime")
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
         languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
      }
   }
}
