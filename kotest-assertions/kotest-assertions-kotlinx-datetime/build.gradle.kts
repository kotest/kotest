plugins {
   id("kotest-multiplatform-library-conventions")
}

kotlin {

   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsShared)
            implementation(libs.kotlinx.datetime)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(projects.kotestRunner.kotestRunnerJunit5)
         }
      }
   }
}
