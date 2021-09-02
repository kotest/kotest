plugins {
   id("java")
   kotlin("jvm")
   id("java-library")
}

repositories {
   mavenCentral()
}

dependencies {
   implementation(project(Projects.Framework.engine))
   implementation(project(Projects.AssertionsShared))
   implementation(project(Projects.JunitRunner))
   implementation(Libs.Coroutines.coreJvm)
}

kotlin {
   sourceSets {
      all {
         languageSettings.optIn("kotlin.time.ExperimentalTime")
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}

tasks.named<Test>("test") {
   useJUnitPlatform()
   filter {
      isFailOnNoMatchingTests = false
   }
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(
         org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
         org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
      )
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
   }
}

apply(from = "../../nopublish.gradle")
