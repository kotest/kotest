plugins {
   id("kotest-jvm-conventions")
   id("kotest-android-native-conventions")
   id("kotest-watchos-device-conventions")
   id("kotest-native-conventions")
   id("kotest-js-conventions")
   id("kotest-wasi-conventions")
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
            // Backs the common Arb.pattern on every target (kotlin-rgxgen-jvm
            // is published at class file version 55 since 0.0.6, so it also
            // works on Kotest's jvmMinTarget = 11).
            implementation(libs.kotlin.rgxgen)
         }
      }

      jvmMain {
         dependencies {
            implementation(libs.diffutils)
            implementation(libs.opentest4j) // used to propagate expected/actual diffs to outer property errors
            // Only kept to back the deprecated JVM-only Arb.stringPattern.
            // Remove together with stringPattern in a future release.
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
   }
}
