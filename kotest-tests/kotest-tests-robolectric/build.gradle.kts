plugins {
   id("kotest-android-conventions")
}

// Robolectric tests use the JUnit4 runner, not JUnit Platform
tasks.withType<Test>().configureEach {
   useJUnit()
}

kotlin {
   androidLibrary {
      namespace = "com.example.kotestdemo"
      compileSdk = 36
      minSdk = 26
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
         }
      }
   }
}
