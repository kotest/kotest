import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
   id("kotlin-conventions")
   id("kotest-jvm-conventions")
   kotlin("plugin.power-assert") version libs.versions.kotlin
}

kotlin {
   sourceSets {
      val jvmTest by getting {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsShared)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestRunner.kotestRunnerJunit5)
         }
      }
   }
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
powerAssert {
   functions = listOf("io.kotest.matchers.shouldBe")
}
