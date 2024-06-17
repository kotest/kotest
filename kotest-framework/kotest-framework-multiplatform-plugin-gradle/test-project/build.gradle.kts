import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
   id("org.jetbrains.kotlin.multiplatform")
   id("io.kotest.multiplatform")
}

repositories {
   mavenCentral()
}

val kotestVersion: String by project
val useNewNativeMemoryModel: String by project

kotlin {

   jvm()

   js {
      browser()
      nodejs()
   }

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

   // Tests run in a separate JVM which does not inherit the JVM arguments from the main build:
   // https://docs.gradle.org/current/userguide/java_testing.html
   maxHeapSize = "3G"
   jvmArgs = listOf("-XX:MaxMetaspaceSize=756m", "-XX:+HeapDumpOnOutOfMemoryError", "-Dfile.encoding=UTF-8")
}

tasks.withType<AbstractTestTask>().configureEach {
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.STANDARD_ERROR, TestLogEvent.STANDARD_OUT)
      exceptionFormat = TestExceptionFormat.FULL
   }
}

if (useNewNativeMemoryModel.toBoolean()) {
   kotlin.targets.withType(KotlinNativeTarget::class.java) {
      binaries.all {
         binaryOptions["memoryModel"] = "experimental"
      }
   }
}

rootProject.plugins.withType(org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin::class.java) {
   // yarn.lock will change when running tests with multiple Kotlin versions
   rootProject.the<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension>().yarnLockMismatchReport =
      org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport.WARNING
}
