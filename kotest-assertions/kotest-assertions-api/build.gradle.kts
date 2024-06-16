import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
   id("kotest-multiplatform-library-conventions")
   id("kotest-android-native-conventions")
   id("kotest-watchos-device-conventions")
}

kotlin {
   @OptIn(ExperimentalKotlinGradlePluginApi::class)
   compilerOptions {
      // TODO Kotest opt-in annotations are missing here...
      //      This module only has one class. Is it necessary?
      optIn.set(emptyList())
   }
}
