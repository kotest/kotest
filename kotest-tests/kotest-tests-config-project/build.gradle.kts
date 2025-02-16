plugins {
   id("kotest-jvm-conventions")
}

kotlin {

   sourceSets {

      val jvmTest by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(projects.kotestRunner.kotestRunnerJunit5)
         }
      }
   }
}
