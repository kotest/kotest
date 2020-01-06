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
            implementation(project(":kotest-core"))
            implementation(project(":kotest-assertions"))
            // we use the internals of the JVM project in the tests
            implementation(project(":kotest-runner:kotest-runner-jvm"))
            implementation(project(":kotest-runner:kotest-runner-junit5"))
            implementation(project(":kotest-assertions:kotest-assertions-arrow"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
            implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.12.1")
            implementation("com.nhaarman:mockito-kotlin:1.6.0")
            implementation("org.mockito:mockito-core:2.24.0")
            // this is here to test that the intellij marker 'dummy' test doesn't appear in intellij
            implementation("org.junit.jupiter:junit-jupiter-engine:5.5.2")
            implementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
            implementation("org.junit.platform:junit-platform-engine:1.5.2")
            implementation("org.junit.platform:junit-platform-suite-api:1.5.2")
            implementation("org.junit.platform:junit-platform-launcher:1.5.2")
         }
      }
   }
}

tasks.named<Test>("jvmTest") {
   useJUnitPlatform()
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(TestLogEvent.FAILED, TestLogEvent.PASSED)
      exceptionFormat = TestExceptionFormat.FULL
   }
}

apply(from = "../../publish.gradle")
