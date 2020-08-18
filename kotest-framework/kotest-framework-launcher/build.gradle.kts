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

   // needed to compile against the engine launcher and discovery
   // but runtime classes will be provided by the dependencies in the users own build
   compileOnly(project(Projects.Api))
   compileOnly(project(Projects.Engine))

   // needed to scan for spec classes
   implementation(project(Projects.Discovery))

   // needed to check for opentest4j.AssertionFailedError so we can add a better error to team city
   implementation(Libs.OpenTest4j.core)

   // needed to parse the command line args
   implementation(Libs.Ajalt.clikt)

   // used to write to the console with fancy colours!
   implementation(Libs.Ajalt.mordant)
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
