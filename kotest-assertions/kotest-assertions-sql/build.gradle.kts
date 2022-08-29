plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      val jvmMain by getting {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsShared)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(libs.mockk)
         }
      }
   }
}
