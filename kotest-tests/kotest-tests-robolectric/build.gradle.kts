plugins {
   id("kotest-android-conventions")
}

kotlin {
   androidLibrary {
      namespace = "com.example.kotestdemo"
      compileSdk = 36
      minSdk = 26

      withHostTestBuilder {}
   }

   sourceSets {
      androidMain {
         dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.appcompat)
         }
      }

      val androidHostTest by getting {
         dependencies {
            implementation(libs.junit4)
            implementation(libs.robolectric)
            // JUnit Vintage Engine runs JUnit4 @RunWith tests via the JUnit Platform
            runtimeOnly(libs.junit.vintage.engine)
            runtimeOnly(libs.junit.platform5.launcher)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            runtimeOnly(projects.kotestRunner.kotestRunnerJunit5)
         }
      }
   }
}
