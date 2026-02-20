@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
   id("kotest-js-conventions")
   id("com.google.devtools.ksp")
   // the Kotest plugin must be a published version and not one in the current build
   id("io.kotest")
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
