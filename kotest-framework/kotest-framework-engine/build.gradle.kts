import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
   id("java")
   id("kotlin-multiplatform")
   id("java-library")
}

repositories {
   mavenCentral()
}

kotlin {

   targets {

      jvm {
         compilations.all {
            kotlinOptions {
               jvmTarget = "1.8"
            }
         }
      }

      js(IR) {
         browser()
         nodejs()
      }

      linuxX64()

      mingwX64()

      macosX64()
      tvos()
      watchosArm32()
      watchosArm64()
      watchosX86()
      watchosX64()
      iosX64()
      iosArm64()
      iosArm32()
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            compileOnly(kotlin("stdlib"))
            implementation(kotlin("reflect"))
            api(project(Projects.AssertionsShared))
            implementation(project(Projects.Common))

            // this is API because we want people to be able to use the functionality in their tests
            // without needing to declare this dependency as well
            api(project(Projects.Api))

            // used to install the debug probes for coroutines
            implementation(Libs.Coroutines.debug)
            implementation(Libs.Coroutines.coreCommon)
         }
      }

      val jsMain by getting {
         dependsOn(commonMain)
         dependencies {
            // this must be api as it's compiled into the final source
            api(kotlin("test-js"))
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            api(Libs.Kotlin.kotlinScriptRuntime)
            implementation(Libs.Kotlin.kotlinScriptUtil)
            implementation(Libs.Kotlin.kotlinScriptJvm)

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
            implementation(project(Projects.AssertionsCore))
            implementation(project(Projects.JunitRunner))
            implementation(Libs.Mocking.mockk)
         }
      }

      val desktopMain by creating {
         dependsOn(commonMain)
      }

      val linuxX64Main by getting {
         dependsOn(desktopMain)
      }

      val macosX64Main by getting {
         dependsOn(desktopMain)
      }

      val mingwX64Main by getting {
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

      val watchosX86Main by getting {
         dependsOn(desktopMain)
      }

      val watchosArm32Main by getting {
         dependsOn(desktopMain)
      }

      val watchosArm64Main by getting {
         dependsOn(desktopMain)
      }

      val watchosX64Main by getting {
         dependsOn(desktopMain)
      }

      val tvosMain by getting {
         dependsOn(desktopMain)
      }

      all {
         languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
         languageSettings.useExperimentalAnnotation("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
   kotlinOptions.jvmTarget = "1.8"
   kotlinOptions.apiVersion = "1.5"
}

tasks.named<Test>("jvmTest") {
   useJUnitPlatform()
   filter {
      isFailOnNoMatchingTests = false
   }
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.STANDARD_ERROR, TestLogEvent.STANDARD_OUT)
      exceptionFormat = TestExceptionFormat.FULL
   }
}


apply(from = "../../publish-mpp.gradle.kts")
