import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
   id("java")
   id("kotlin-multiplatform")
   id("java-library")
   id("com.adarshr.test-logger")
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

      val jvmMain by getting {
         dependencies {
            implementation(kotlin("stdlib-jdk8"))
            api(project(":kotest-fp"))
            api(project(":kotest-core"))
//            api(project(":kotest-runner:kotest-runner-console"))
            api(project(":kotest-runner:kotest-runner-jvm"))
            api(Libs.JUnitPlatform.engine)
            api(Libs.JUnitPlatform.api)
            api(Libs.JUnitPlatform.launcher)
            api(Libs.JUnitJupiter.api)
            api(Libs.Slf4j.api)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(project(":kotest-core"))
            implementation(project(":kotest-assertions"))
            implementation(project(":kotest-runner:kotest-runner-jvm"))
            implementation(project(":kotest-runner:kotest-runner-junit5"))
            implementation(Libs.JUnitPlatform.testkit)
            implementation(Libs.Slf4j.api)
            implementation(Libs.Logback.classic)
         }
      }
   }
}

tasks.named<Test>("jvmTest") {
   useJUnitPlatform()
   filter {
      setFailOnNoMatchingTests(false)
   }
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED, org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED)
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
   }
}

apply(from = "../../publish.gradle")
