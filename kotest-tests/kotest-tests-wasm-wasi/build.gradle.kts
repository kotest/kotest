@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
   id("kotest-js-wasm-conventions")
   id("com.google.devtools.ksp").version("2.3.4")
   // the Kotest plugin must be a published version and not one in the current build
   id("io.kotest").version("6.1.2")
}

kotlin {
   sourceSets {
      commonTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }
   }
}
