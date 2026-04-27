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
      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
   }
   packaging {
      resources {
         excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE*.md}"
      }
   }
   buildFeatures {
      compose = true
   }
   testOptions {
      unitTests.all {
         it.useJUnitPlatform()
      }
      animationsDisabled = true
      managedDevices {
         localDevices {
            create("pixel6Api34") {
               device = "Pixel 6"
               apiLevel = 34
               systemImageSource = "aosp_atd"
            }
         }
      }
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

// Run instrumented tests on the managed emulator as part of the standard check lifecycle.
// Use: ./gradlew pixel2Api30DebugAndroidTest
// The managed device handles the full AVD lifecycle (download image, boot, test, shutdown).
tasks.named("check") {
   dependsOn("pixel6Api34DebugAndroidTest")
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
   debugImplementation(libs.androidx.ui.test.manifest)
}
