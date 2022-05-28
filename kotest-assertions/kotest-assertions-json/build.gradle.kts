plugins {
   `java-library`
   kotlin("multiplatform")
   kotlin("plugin.serialization")
}

kotlin {

   targets {
      jvm()
      js(IR) {
         browser()
         nodejs()
      }

      linuxX64()

      mingwX64()

      macosX64()
      macosArm64()

      tvos()
      tvosSimulatorArm64()

      watchosArm32()
      watchosArm64()
      watchosX86()
      watchosX64()
      watchosSimulatorArm64()

      iosX64()
      iosArm64()
      iosArm32()
      iosSimulatorArm64()
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(project(Projects.Common))
            implementation(project(Projects.Assertions.Shared))
            implementation(project(Projects.Assertions.Core))
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(project(Projects.Framework.api))
            implementation(project(Projects.Framework.datatest))
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Property))
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(libs.jayway.json.path)
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
         languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
         languageSettings.optIn("kotlin.RequiresOptIn")
      }
   }
}

apply(from = "../../publish-mpp.gradle.kts")
