plugins {
   id("java")
   kotlin("jvm")
   id("java-library")
}

repositories {
   mavenCentral()
}

dependencies {
   implementation(project(Projects.Engine))
   implementation(project(Projects.AssertionsShared))
   implementation(project(Projects.JunitRunner))
   implementation(Libs.Coroutines.coreJvm)
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
