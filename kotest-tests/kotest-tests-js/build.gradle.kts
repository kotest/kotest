plugins {
   id("kotest-js-conventions")
   id("com.google.devtools.ksp") version "2.2.21-2.0.4"
   // must be a published version and not one in the current build
   id("io.kotest").version("6.1.0-LOCAL")
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
