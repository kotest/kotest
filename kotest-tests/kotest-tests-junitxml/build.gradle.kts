plugins {
   id("kotest-jvm-conventions")
}

kotlin {

   sourceSets {
      jvmTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(libs.jdom2)
         }
      }
   }
}

val testResultsDir = layout.buildDirectory.dir("test-results").get()

tasks.withType<Test>().configureEach {
   systemProperty("kotest.framework.config.fqn", "com.sksamuel.kotest.ProjectConfig")

   // There are multiple TestRuns (for separate JDKs), so use a separate report directory for each test.
   // Don't register the value as a task input because the directory doesn't affect the result of the test.
   val taskTestResultsDir = testResultsDir.dir(name)

   systemProperty("taskTestResultsDir", taskTestResultsDir.asFile.invariantSeparatorsPath)

   doFirst {
      // First clean old results, to prevent results from previous tests interfering with the current test.
      taskTestResultsDir.asFile.deleteRecursively()
   }
}
