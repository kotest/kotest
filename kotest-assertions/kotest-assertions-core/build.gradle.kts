plugins {
   id("java")
   kotlin("multiplatform")
   id("java-library")

}

kotlin {

   targets {
      jvm()
      js(BOTH) {
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
            compileOnly(kotlin("stdlib"))
            implementation(kotlin("reflect"))
            implementation(Libs.Coroutines.coreCommon)
            implementation(project(Projects.Common))
            implementation(project(Projects.Assertions.Api))
            // this is api because we want to expose `shouldBe` etc
            api(project(Projects.Assertions.Shared))
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(Libs.Coroutines.jdk8)
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.Property))
            implementation(project(Projects.JunitRunner))
            implementation(Libs.Coroutines.coreJvm)
            implementation(Libs.OpenTest4j.opentest4j)
            implementation(Libs.Apache.commonslang)
            implementation(Libs.Mocking.mockk)
         }
      }

      all {
         languageSettings.optIn("kotlin.time.ExperimentalTime")
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
         languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
      }
   }
}

apply(from = "../../publish-mpp.gradle.kts")
