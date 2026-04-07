@file:OptIn(ExperimentalBuildToolsApi::class)

import org.jetbrains.kotlin.buildtools.api.ExperimentalBuildToolsApi
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
   id("kotest-base")
   id("com.android.library")
   alias(libs.plugins.jetbrains.kotlin.compose)
}

val catalog: VersionCatalog = versionCatalogs.named("libs")

android {
   namespace = "io.kotest.tests.android.instrumented"
   compileSdk = 36
   defaultConfig {
      minSdk = 24
   }
}

kotlin {
   compilerOptions {

      freeCompilerArgs.add("-Xexpect-actual-classes")

      // See https://mbonnin.net/2026-02-22-kotlin-versions
      @Suppress("OPT_IN_USAGE")
      compilerVersion.set(catalog.findVersion("kotlin-compile-version").get().toString())
      coreLibrariesVersion = catalog.findVersion("kotlin-core-libaries-version").get().toString()

      apiVersion.set(
         KotlinVersion.fromVersion(
            catalog.findVersion("kotlin-language-version").get().toString().substringBeforeLast('.')
         )
      )

      languageVersion.set(
         KotlinVersion.fromVersion(
            catalog.findVersion("kotlin-language-version").get().toString().substringBeforeLast('.')
         )
      )

      allWarningsAsErrors = false
   }
   sourceSets.configureEach {
      languageSettings {
         optIn("io.kotest.common.KotestInternal")
         optIn("kotlin.contracts.ExperimentalContracts")
         optIn("kotlin.experimental.ExperimentalTypeInference")
         optIn("kotlin.time.ExperimentalTime")
      }
   }
}

dependencies {
   implementation(projects.kotestFramework.kotestFrameworkEngine)
   implementation(projects.kotestRunner.kotestRunnerJunit4)
   implementation(projects.kotestAssertions.kotestAssertionsCore)

   implementation(libs.androidx.core.ktx)
   implementation(libs.androidx.appcompat)
   implementation(libs.material)

   androidTestImplementation(libs.androidx.junit)
   androidTestImplementation(libs.androidx.test.runner)
   androidTestImplementation(libs.androidx.ui.test.junit4)
}
