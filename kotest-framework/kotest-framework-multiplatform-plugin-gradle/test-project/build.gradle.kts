import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

// Use a Node.js version current enough to support Kotlin/Wasm

rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
   rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
//      nodeVersion = "21.0.0-v8-canary202309143a48826a08"
      nodeVersion = "22.0.0-v8-canary20231213fc7703246e"
      println("Using Node.js $nodeVersion to support Kotlin/Wasm")
      nodeDownloadBaseUrl = "https://nodejs.org/download/v8-canary"
      System.setProperty("nodeJsCanaryConfigured", "true")
   }
}

rootProject.tasks.withType<org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask>().configureEach {
   args.add("--ignore-engines") // Prevent Yarn from complaining about newer Node.js versions.
}
