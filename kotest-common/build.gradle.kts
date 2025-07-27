import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyTemplate

plugins {
   id("kotest-jvm-conventions")
   id("kotest-js-wasm-conventions")
   id("kotest-native-conventions")
   id("kotest-android-native-conventions")
   id("kotest-watchos-device-conventions")
   id("kotest-publishing-conventions")
}

kotlin {

   @OptIn(ExperimentalKotlinGradlePluginApi::class)
   applyHierarchyTemplate(KotlinHierarchyTemplate.default) {
      group("common") {
         // contains no-op implemntations of reflection based functionality on non-jvm targets
         group("basic") {
            withJs()
            withWasmJs()
            withWasmWasi()
            withNative()
         }
      }
   }

   sourceSets {

      commonMain {
         dependencies {
            implementation(libs.kotlin.reflect)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.coroutines.test)
         }
      }

      commonTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }
   }
}
