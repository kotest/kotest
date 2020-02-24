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
            freeCompilerArgs + "-Xuse-experimental=kotlin.Experimental"
         }
      }
   }

   sourceSets {

      val jvmTest by getting {
         dependencies {
            implementation(kotlin("stdlib-jdk8"))
            implementation(kotlin("reflect"))
            implementation(project(":kotest-core"))
            implementation(project(":kotest-assertions"))
            implementation(project(":kotest-runner:kotest-runner-junit5"))
            implementation(Libs.Coroutines.core)
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

