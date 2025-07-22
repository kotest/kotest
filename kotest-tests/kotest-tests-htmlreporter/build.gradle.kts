plugins {
   id("kotest-jvm-conventions")
}

kotlin {

   sourceSets {
      jvmTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(projects.kotestRunner.kotestRunnerJunit5)
            implementation(projects.kotestExtensions.kotestExtensionsHtmlreporter)
            implementation(libs.jdom2)
         }
      }
   }
}
