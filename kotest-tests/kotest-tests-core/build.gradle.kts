import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

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

      val jvmMain by getting {
         dependencies {
            implementation(project(":kotest-core"))
            implementation(project(":kotest-assertions"))
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            // we use the internals of the JVM project in the tests
            implementation(project(":kotest-runner:kotest-runner-jvm"))
            implementation(project(":kotest-runner:kotest-runner-junit5"))
            implementation(project(":kotest-assertions:kotest-assertions-arrow"))
            implementation(Libs.Log4j.api)
            implementation("com.nhaarman:mockito-kotlin:1.6.0")
            implementation("org.mockito:mockito-core:2.24.0")
            // this is here to test that the intellij marker 'dummy' test doesn't appear in intellij
            implementation(Libs.JUnitJupiter.engine)
            implementation(Libs.JUnitJupiter.api)
            implementation(Libs.JUnitPlatform.engine)
            implementation(Libs.JUnitPlatform.api)
            implementation(Libs.JUnitPlatform.launcher)
         }
      }
   }
}

tasks.named<Test>("jvmTest") {
   useJUnitPlatform()
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(FAILED, PASSED)
      exceptionFormat = TestExceptionFormat.FULL
   }
}
