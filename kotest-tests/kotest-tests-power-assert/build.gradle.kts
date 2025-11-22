import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
   id("kotlin-conventions")
   id("kotest-jvm-conventions")
   id("linux-only-tests-conventions")
   alias(libs.plugins.power.assert)
}

kotlin {
   sourceSets {
      jvmTest {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
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
