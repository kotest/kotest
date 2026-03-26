@file:Suppress("UnstableApiUsage")

import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
   id("kotest-base")
   kotlin("jvm")
   `jvm-test-suite`
}

kotlin {
   jvmToolchain(11)
}

testing {
   suites {
      val test by getting(JvmTestSuite::class) {
         dependencies {
            implementation(projects.kotestRunner.kotestRunnerJunit5)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }
      val integrationTest by registering(JvmTestSuite::class) {
         dependencies {
            implementation(projects.kotestRunner.kotestRunnerJunit5)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }
      val functionalTest by registering(JvmTestSuite::class) {
         dependencies {
            implementation(projects.kotestRunner.kotestRunnerJunit5)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }
   }
}

tasks.withType<Test>().configureEach {
   useJUnitPlatform()
   outputs.upToDateWhen { false }
   filter { isFailOnNoMatchingTests = false }
   testLogging { events(TestLogEvent.FAILED) }
   systemProperty(
      "suiteXmlDir",
      layout.buildDirectory.dir("test-results/$name").get().asFile.invariantSeparatorsPath,
   )
}

tasks.named("check") {
   dependsOn(testing.suites)
}
