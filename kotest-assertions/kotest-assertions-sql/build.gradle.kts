plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      val jvmMain by getting {
         dependencies {
            implementation(project(Projects.Assertions.Shared))
            implementation(project(Projects.Assertions.Core))
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(libs.mockk)
         }
      }
   }
}
