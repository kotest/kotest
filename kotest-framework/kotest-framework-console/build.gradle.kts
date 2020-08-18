import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
   id("java")
   kotlin("jvm")
   id("java-library")
}

repositories {
   mavenCentral()
}

dependencies {

   implementation(project(Projects.Api))

   // needed to parse the command line args
   implementation(Libs.Ajalt.clikt)

   // used to write to the console with fancy colours!
   implementation(Libs.Ajalt.mordant)

   // needed to check for opentest4j.AssertionFailedError so we can add a better error to team city
   implementation(Libs.OpenTest4j.core)

   testImplementation(project(Projects.AssertionsCore))
   testImplementation(project(Projects.JunitRunner))
   testImplementation(Libs.Mocking.mockk)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
   kotlinOptions.jvmTarget = "1.8"
}

tasks.named<Test>("test") {
   useJUnitPlatform()
   filter {
      isFailOnNoMatchingTests = false
   }
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(TestLogEvent.FAILED, TestLogEvent.PASSED)
      exceptionFormat = TestExceptionFormat.FULL
   }
}


apply(from = "../../publish-mpp.gradle.kts")
