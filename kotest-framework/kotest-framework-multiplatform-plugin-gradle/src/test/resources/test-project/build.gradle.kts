import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
   id("org.jetbrains.kotlin.multiplatform")
   id("io.kotest.multiplatform")
}

repositories {
   mavenCentral()
}

val kotestVersion: String by project

kotlin {
   jvm()

   js(IR) {
      browser()
      nodejs()
   }

   linuxX64()
   macosX64()
   macosArm64()
   mingwX64()

   sourceSets {
      val commonTest by getting {
         dependencies {
            implementation("io.kotest:kotest-assertions-core:$kotestVersion")
            implementation("io.kotest:kotest-framework-api:$kotestVersion")
            implementation("io.kotest:kotest-framework-engine:$kotestVersion")
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation("io.kotest:kotest-runner-junit5:$kotestVersion")
         }
      }
   }
}

tasks.named<Test>("jvmTest") {
   useJUnitPlatform()
}

tasks.withType<AbstractTestTask>().configureEach {
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.STANDARD_ERROR, TestLogEvent.STANDARD_OUT)
      exceptionFormat = TestExceptionFormat.FULL
   }
}
