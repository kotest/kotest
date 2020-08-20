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
   }

   targets.all {
      compilations.all {
         kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
         }
      }
   }

   sourceSets {

      val jvmMain by getting {
         dependencies {

            // needed to compile against the engine launcher and discovery
            // but runtime classes will be provided by the dependencies in the users own build
            compileOnly(project(Projects.Api))
            compileOnly(project(Projects.Engine))

            // needed to scan for spec classes
            implementation(project(Projects.Discovery))

            // needed to check for opentest4j.AssertionFailedError so we can add a better error to team city
            implementation(Libs.OpenTest4j.core)

            // needed to parse the command line args
            implementation(Libs.Ajalt.clikt)

            // used to write to the console with fancy colours!
            implementation(Libs.Ajalt.mordant)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.AssertionsCore))
            implementation(project(Projects.JunitRunner))
         }
      }
   }
}

tasks.named<Test>("jvmTest") {
   useJUnitPlatform()
   filter {
      isFailOnNoMatchingTests = false
   }
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(TestLogEvent.FAILED, TestLogEvent.PASSED)
      exceptionFormat = TestExceptionFormat.FULL
   }
}

apply(from = "../../publish-mpp.gradle.kts")
