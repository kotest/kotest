plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      jvmMain {
         dependencies {
            api(projects.kotestAssertions.kotestAssertionsCore)
            implementation(libs.jsoup)
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestRunner.kotestRunnerJunit5)
         }
      }
   }
}
