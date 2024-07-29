import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
   id("org.jetbrains.kotlin.multiplatform")
   id("io.kotest.multiplatform")
}

val kotestVersion: String by project

repositories {
   val devMavenRepoPath: String by project
   exclusiveContent {
      forRepository {
         maven(file(devMavenRepoPath)) {
            name = "DevMavenRepo"
         }
      }
      filter {
         includeGroupAndSubgroups("io.kotest")
      }
   }
   mavenCentral()
}

kotlin {
   jvmToolchain(8)

   jvm()

   js {
      browser()
      nodejs()
   }

   @OptIn(org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl::class)
   wasmJs {
      browser()
      nodejs()
   }

   /* FIXME: enable wasmWasi when there is support in kotlinx-coroutines-core (1.8.0-RC does only wasmJs)
   wasmWasi {
      nodejs()
   }
   */

   linuxX64()
   macosX64()
   macosArm64()
   mingwX64()

   sourceSets {
      commonTest {
         dependencies {
            implementation("io.kotest:kotest-assertions-core:$kotestVersion")
            implementation("io.kotest:kotest-framework-api:$kotestVersion")
            implementation("io.kotest:kotest-framework-engine:$kotestVersion")
         }
      }

      jvmTest {
         dependencies {
            implementation("io.kotest:kotest-runner-junit5:$kotestVersion")
         }
      }
   }
}

tasks.withType<Test>().configureEach {
   useJUnitPlatform()

   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.STANDARD_ERROR, TestLogEvent.STANDARD_OUT)
      exceptionFormat = TestExceptionFormat.FULL
   }

   systemProperty("kotest.framework.classpath.scanning.autoscan.disable", "true")

   // Register the test results dir as an output, so Gradle will cache it
   outputs
      .dir(layout.buildDirectory.dir("test-results"))
      .withPropertyName("testResultsDir")
   // Always cache the test results
   outputs.cacheIf { true }
}

plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin>().configureEach {
   // yarn.lock will change when running tests with multiple Kotlin versions
   extensions.configure<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension> {
      yarnLockMismatchReport = org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport.WARNING
   }
}
