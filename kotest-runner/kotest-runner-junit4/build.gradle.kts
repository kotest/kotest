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

      val jvmCommonMain by creating {
         dependencies {
            api(projects.kotestFramework.kotestFrameworkEngine)
            api(libs.junit4)
         }
      }

      androidMain {
         dependsOn(jvmCommonMain)
         dependencies {
            api(libs.androidx.test.runner)
         }
      }

      jvmMain {
         dependsOn(jvmCommonMain)
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
