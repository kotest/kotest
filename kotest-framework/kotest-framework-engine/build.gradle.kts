plugins {
   id("kotest-jvm-conventions")
   id("kotest-js-wasm-conventions")
   id("kotest-native-conventions")
   id("kotest-android-native-conventions")
   id("kotest-watchos-device-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {

      commonMain {
         dependencies {
            api(projects.kotestAssertions.kotestAssertionsShared)
            implementation(libs.kotlin.reflect)
            api(projects.kotestCommon) // needs to be API so the domain objects are open

            api(libs.kotlinx.coroutines.core)

            // used for the test scheduler
            implementation(libs.kotlinx.coroutines.test)
         }
      }

      jsMain {
         // used to write to the console with fancy colours!
         dependencies {
            implementation(libs.mordant)
         }
      }

      jvmMain {
         dependencies {

            // we use AssertionFailedError from opentest4j
            api(libs.opentest4j)

            // used to write to the console with fancy colours!
            api(libs.mordant)

            // used to install the debug probes for coroutines
            api(libs.kotlinx.coroutines.debug)
         }
      }

      jvmTest {
         dependencies {
            implementation(kotlin("stdlib"))
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(projects.kotestProperty)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.mockk)
            implementation(libs.junit.platform.engine)
            implementation(libs.junit.platform.api)
            implementation(libs.junit.platform.launcher)
            implementation(libs.junit.jupiter.api)
            // this is here to test that the intellij marker 'dummy' test doesn't appear in intellij
            implementation(libs.junit.jupiter.engine)
         }
      }

      linuxX64Main {
         dependencies {
            // used to write to the console with fancy colours!
            implementation(libs.mordant)
         }
      }
   }
}

tasks.withType<Test>().configureEach {
   jvmArgumentProviders.add(CommandLineArgumentProvider {
      val javaLauncher = javaLauncher.orNull
      buildList {
         if (javaLauncher != null && javaLauncher.metadata.languageVersion >= JavaLanguageVersion.of(9)) {
            // --add-opens is only available in Java 9+
            add("--add-opens=java.base/java.util=ALL-UNNAMED")
            add("--add-opens=java.base/java.lang=ALL-UNNAMED")
         }
      }
   })
}
