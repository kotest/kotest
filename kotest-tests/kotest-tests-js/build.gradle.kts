plugins {
   id("kotest-js-conventions")
   id("com.google.devtools.ksp") version "2.2.21-2.0.4"
   // must be a published version and not one in the current build
   // this module is commented out from settings.gradle.kts because it needs a published plugin to work
   // so once 6.1 is out I will come back and uncomment this module and get the JS tests working.
   id("io.kotest").version("TODO")
}

kotlin {
   sourceSets {
      commonTest {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }
   }
}
