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

            // this is API because we want people to be able to use the functionality in their tests
            // without needing to declare this dependency as well
            api(projects.kotestFramework.kotestFrameworkApi)

            // used to install the debug probes for coroutines
            implementation(libs.kotlinx.coroutines.debug)
            implementation(libs.kotlinx.coroutines.core)
            // used for the test scheduler
            implementation(libs.kotlinx.coroutines.test)
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(libs.kotlinx.coroutines.test)

            api(libs.classgraph)

            // needed to scan for spec classes
            api(projects.kotestFramework.kotestFrameworkDiscovery)

            // we use AssertionFailedError from opentest4j
            implementation(libs.opentest4j)

            // used to write to the console with fancy colours!
            api(libs.mordant)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(kotlin("stdlib"))
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(projects.kotestProperty)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.mockk)
         }
      }
   }
}

tasks.withType<Test>().configureEach {
   jvmArgs("--add-opens=java.base/java.util=ALL-UNNAMED", "--add-opens=java.base/java.lang=ALL-UNNAMED")

   systemProperty("kotest.framework.classpath.scanning.autoscan.disable", "false")
}
