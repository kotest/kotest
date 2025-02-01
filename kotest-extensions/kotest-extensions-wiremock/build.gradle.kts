plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      val jvmMain by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            api(libs.wiremock)
         }
      }
      val jvmTest by getting {
         dependencies {
//            implementation(libs.kotest.assertions)
            implementation(libs.fuel)
         }
      }
   }
}
