plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      jvmMain {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            api(libs.wiremock)
         }
      }
      jvmTest {
         dependencies {
            implementation(libs.fuel)
         }
      }
   }
}
