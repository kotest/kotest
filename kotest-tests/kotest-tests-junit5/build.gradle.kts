import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

buildscript {
   repositories {
      mavenCentral()
      mavenLocal()
   }
}

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
            implementation(project(":kotest-core"))
            implementation(project(":kotest-assertions"))
            //implementation(project(":kotest-runner:kotest-runner-console"))
            implementation(project(":kotest-runner:kotest-runner-junit5"))
            implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.12.1")
            implementation("org.junit.platform:junit-platform-testkit:1.5.1")
         }
      }
   }
}

tasks {
   test {
      useJUnitPlatform()
      testLogging {
         showExceptions = true
         showStandardStreams = true
         events = setOf(TestLogEvent.FAILED, TestLogEvent.PASSED)
         exceptionFormat = TestExceptionFormat.FULL
      }
   }
}
