buildscript {
   repositories {
      mavenCentral()
      mavenLocal()
      google()
      gradlePluginPortal()
   }
}

plugins {
   id("java")
   kotlin("multiplatform")
   id("java-library")
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
            compileOnly(kotlin("stdlib"))
            implementation(kotlin("reflect"))
            api(project(Projects.Assertions.Shared))
            implementation(project(Projects.Common))

            // this is API because we want people to be able to use the functionality in their tests
            // without needing to declare this dependency as well
            api(project(Projects.Framework.api))

            // used to install the debug probes for coroutines
            implementation(Libs.Coroutines.debug)
            implementation(Libs.Coroutines.coreCommon)
            // used for the test scheduler
            implementation(Libs.Coroutines.test)
         }
      }

      val jsMain by getting {
         dependsOn(commonMain)
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
//            api(Libs.Kotlin.kotlinScriptRuntime)
//            implementation(Libs.Kotlin.kotlinScriptUtil)
//            implementation(Libs.Kotlin.kotlinScriptJvm)
            implementation(Libs.Coroutines.test)

            api(Libs.Classgraph.classgraph)

            // needed to scan for spec classes
            api(project(Projects.Discovery))

            // we use AssertionFailedError from opentest4j
            implementation(Libs.OpenTest4j.opentest4j)

            // used to write to the console with fancy colours!
            api(Libs.Ajalt.mordant)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.Assertions.Core))
            implementation(project(Projects.JunitRunner))
            implementation(Libs.Coroutines.coreJvm)
            implementation(Libs.Mocking.mockk)
         }
      }

      val desktopMain by creating {
         dependsOn(commonMain)
      }

      val macosX64Main by getting {
         dependsOn(desktopMain)
      }

      val macosArm64Main by getting {
         dependsOn(desktopMain)
      }

      val mingwX64Main by getting {
         dependsOn(desktopMain)
      }

      val linuxX64Main by getting {
         dependsOn(desktopMain)
      }

      val iosX64Main by getting {
         dependsOn(desktopMain)
      }

      val iosArm64Main by getting {
         dependsOn(desktopMain)
      }

      val iosArm32Main by getting {
         dependsOn(desktopMain)
      }

      val iosSimulatorArm64Main by getting {
         dependsOn(desktopMain)
      }

      val watchosArm32Main by getting {
         dependsOn(desktopMain)
      }

      val watchosArm64Main by getting {
         dependsOn(desktopMain)
      }

      val watchosX86Main by getting {
         dependsOn(desktopMain)
      }

      val watchosX64Main by getting {
         dependsOn(desktopMain)
      }

      val watchosSimulatorArm64Main by getting {
         dependsOn(desktopMain)
      }

      val tvosMain by getting {
         dependsOn(desktopMain)
      }

      val tvosSimulatorArm64Main by getting {
         dependsOn(desktopMain)
      }

      all {
         languageSettings.optIn("kotlin.time.ExperimentalTime")
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}

apply(from = "../../publish-mpp.gradle.kts")
