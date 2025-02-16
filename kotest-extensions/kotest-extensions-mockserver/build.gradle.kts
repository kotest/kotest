plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {

   sourceSets {

      val jvmMain by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            api(libs.mockserver.netty)
            api(libs.mockserver.client.java)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(projects.kotestRunner.kotestRunnerJunit5)
            implementation(libs.fuel)
            implementation(libs.mockk)
         }
      }
   }
}
