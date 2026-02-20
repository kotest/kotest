@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
   id("kotlin-conventions")
   // using a published version
   id("io.kotest")
   id("com.google.devtools.ksp")
}

kotlin {

   jvm()

//   js {
//      browser()
//      nodejs()
//   }
//
//   linuxX64()
//   mingwX64()
//
//   wasmJs {
//      browser()
//      nodejs()
//      d8()
//   }
//
//   wasmWasi {
//      nodejs()
//   }

   sourceSets {
      commonTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(kotlin("test"))
         }
      }
      jvmTest {
         dependencies {
            implementation(projects.kotestRunner.kotestRunnerJunit5)
         }
      }
   }
}

tasks.withType<Test>().configureEach {
   useJUnitPlatform()
}
