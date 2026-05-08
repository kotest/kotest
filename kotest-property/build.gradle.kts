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

      // Every non-JVM target uses kotlin-rgxgen for Arb.stringPattern.
      // The `nonjvm` source set is established by the kotest-js / kotest-native /
      // kotest-android-native conventions via their KotlinHierarchyTemplate group.
      // findByName is used because the source set is absent in -PjvmOnly=true mode,
      // where the conventions skip their applyHierarchyTemplate calls.
      findByName("nonjvmMain")?.dependencies {
         implementation(libs.kotlin.rgxgen)
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
