@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
   id("kotest-js-conventions")
   id("com.google.devtools.ksp").version("2.3.6")
   // the Kotest plugin must be a published version and not one in the current build
   id("io.kotest").version("6.1.3")
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
