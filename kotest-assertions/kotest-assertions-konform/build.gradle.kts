plugins {
   id("kotest-multiplatform-library-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      commonMain {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(libs.konform)
         }
      }
      commonTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }
   }
}
