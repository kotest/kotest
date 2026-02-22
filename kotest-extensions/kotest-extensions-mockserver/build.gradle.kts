plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      jvmMain {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            api(libs.mockserver.netty)
            api(libs.mockserver.client.java)
         }
      }
      jvmTest {
         dependencies {
            implementation(projects.kotestRunner.kotestRunnerJunit5)
            implementation(libs.fuel)
            implementation(libs.mockk)
         }
      }
   }
}
