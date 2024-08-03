import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
   id("kotest-multiplatform-library-conventions")
   id("kotest-android-native-conventions")
   id("kotest-watchos-device-conventions")
   kotlin("plugin.power-assert") version "2.0.0"
}

kotlin {

   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(kotlin("reflect"))
            implementation(projects.kotestCommon)
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(libs.kotlinx.coroutines.jdk8)
            implementation(libs.diffutils)
            implementation(libs.opentest4j)
            implementation(libs.apache.commons.lang)
         }
      }
   }
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
powerAssert {
   functions = listOf("io.kotest.matchers.shouldBe")
}
