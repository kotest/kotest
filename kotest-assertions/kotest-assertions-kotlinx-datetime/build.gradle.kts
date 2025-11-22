plugins {
   id("kotest-multiplatform-library-conventions")
   id("kotest-publishing-conventions")
   id("linux-only-tests-conventions")
}

kotlin {

   sourceSets {

      commonMain {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(libs.kotlinx.datetime)
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestRunner.kotestRunnerJunit5)
         }
      }
   }
}
