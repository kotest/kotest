plugins {
   id("kotest-jvm-conventions")
   id("kotest-android-conventions")
   id("kotest-publishing-conventions")
}

kotlin {

   androidLibrary {
      namespace = "io.kotest.runner.junit4"
      compileSdk = 34
      withHostTestBuilder {}
   }

   sourceSets {
      jvmMain {
         dependencies {
            api(projects.kotestFramework.kotestFrameworkEngine)
            api(libs.junit4)
         }
      }

      androidMain {
         dependencies {
            api(projects.kotestFramework.kotestFrameworkEngine)
            api(libs.junit4)
            api(libs.androidx.test.runner)
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }

      val androidHostTest by getting {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(projects.kotestRunner.kotestRunnerJunit5)
            implementation(libs.mockk)
         }
      }
   }
}
