plugins {
   id("kotest-multiplatform-library-conventions")
   id("kotest-android-native-conventions")
   id("kotest-watchos-device-conventions")
}

kotlin {
   sourceSets {

      val commonMain by getting {
         dependencies {
            api(projects.kotestAssertions.kotestAssertionsShared)
            implementation(kotlin("reflect"))
            api(projects.kotestCommon) // needs to be API so the domain objects are open

            api(libs.kotlinx.coroutines.core)
            // used for the test scheduler
            implementation(libs.kotlinx.coroutines.test)
         }
      }

      val jvmMain by getting {
         dependencies {

            // we use AssertionFailedError from opentest4j
            api(libs.opentest4j)

            // used to write to the console with fancy colours!
            api(libs.mordant)

            // used to install the debug probes for coroutines
            api(libs.kotlinx.coroutines.debug)
         }
      }

      val jvmTest by getting {
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

   systemProperty("kotest.framework.classpath.scanning.autoscan.disable", "false")
}
