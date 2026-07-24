plugins {
   id("kotest-android-conventions")
}

kotlin {
   androidLibrary {
      namespace = "com.example.kotest"
      compileSdk = 36
      minSdk = 26

      withHostTestBuilder {}
   }

   sourceSets {
      androidMain {
         dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.appcompat)
            // RobolectricExtension lives in androidMain so it can be reused by downstream
            // Robolectric-based test modules. Robolectric and the Kotest engine are
            // therefore compile-time deps of androidMain. JUnit 4 is a transitive dep of
            // Robolectric (the RobolectricTestRunner we subclass extends BlockJUnit4ClassRunner).
            implementation(libs.junit4)
            implementation(libs.robolectric)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }

      val androidHostTest by getting {
         dependencies {
            // JUnit Vintage Engine runs JUnit4 @RunWith tests via the JUnit Platform
            runtimeOnly(libs.junit.vintage.engine)
            runtimeOnly(libs.junit.platform5.launcher)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            runtimeOnly(projects.kotestRunner.kotestRunnerJunit5)
         }
      }
   }
}
