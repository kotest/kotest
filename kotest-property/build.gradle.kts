plugins {
   id("kotest-jvm-conventions")
   id("kotest-android-native-conventions")
   id("kotest-watchos-device-conventions")
   id("kotest-native-conventions")
   id("kotest-js-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {

      commonMain {
         dependencies {
            implementation(libs.kotlin.reflect)
            api(projects.kotestCommon)
            api(projects.kotestAssertions.kotestAssertionsCore)
            api(projects.kotestFramework.kotestFrameworkEngine)
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      jvmMain {
         dependencies {
            implementation(libs.diffutils)
            implementation(libs.opentest4j) // used to propagate expected/actual diffs to outer property errors
            api(libs.rgxgen)
         }
      }

      commonTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(projects.kotestAssertions.kotestAssertionsTable)
         }
      }

      // ------------------------------------------------------------------
      // Intermediate source sets that route each non-JVM target to one of
      // two implementations of Arb.stringPattern:
      //   - rgxgenSupported*: backed by community.flock.kotlinx.rgxgen
      //   - rgxgenUnsupported*: throws UnsupportedOperationException
      //
      // The two groups are wired in addition to (not in place of) the
      // hierarchy template used by kotest-js/native/android-native
      // conventions; each leaf source set ends up with multiple parents,
      // which Kotlin handles fine.
      // ------------------------------------------------------------------

      val rgxgenSupportedMain by creating {
         dependsOn(commonMain.get())
         dependencies {
            implementation(libs.kotlin.rgxgen)
         }
      }
      val rgxgenUnsupportedMain by creating {
         dependsOn(commonMain.get())
      }
      val rgxgenSupportedTest by creating {
         dependsOn(commonTest.get())
      }
      val rgxgenUnsupportedTest by creating {
         dependsOn(commonTest.get())
      }

      // JS targets (always present via kotest-js-conventions when not jvmOnly)
      findByName("jsMain")?.dependsOn(rgxgenSupportedMain)
      findByName("jsTest")?.dependsOn(rgxgenSupportedTest)

      findByName("wasmJsMain")?.dependsOn(rgxgenUnsupportedMain)
      findByName("wasmJsTest")?.dependsOn(rgxgenUnsupportedTest)

      // Native targets (only present when Kotlin Native is enabled)
      val supportedNativeTargets = listOf(
         "linuxX64",
         "macosArm64",
         "mingwX64",
      )
      val unsupportedNativeTargets = listOf(
         "linuxArm64",
         "iosX64", "iosArm64", "iosSimulatorArm64",
         "tvosArm64", "tvosSimulatorArm64",
         "watchosArm32", "watchosArm64",
         "watchosSimulatorArm64", "watchosDeviceArm64",
         "androidNativeX86", "androidNativeX64", "androidNativeArm64",
      )
      supportedNativeTargets.forEach { target ->
         findByName("${target}Main")?.dependsOn(rgxgenSupportedMain)
         findByName("${target}Test")?.dependsOn(rgxgenSupportedTest)
      }
      unsupportedNativeTargets.forEach { target ->
         findByName("${target}Main")?.dependsOn(rgxgenUnsupportedMain)
         findByName("${target}Test")?.dependsOn(rgxgenUnsupportedTest)
      }
   }
}
