plugins {
   id("kotest-jvm-conventions")
}

kotlin {
   sourceSets {
      jvmMain {
         dependencies {
            // putting this in main so it can also be extended by kotest-tests-config-project-inherited
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }
      jvmTest {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(projects.kotestRunner.kotestRunnerJunit5)
         }
      }
   }
}
