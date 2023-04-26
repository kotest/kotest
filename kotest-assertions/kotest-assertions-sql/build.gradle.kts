plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      jvmMain {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsShared)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }

      jvmTest {
         dependencies {
            implementation(libs.mockk)
         }
      }
   }
}
