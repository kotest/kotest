plugins {
   id("java")
   kotlin("jvm")
   id("java-library")
}

repositories {
   mavenCentral()
}

dependencies {
   implementation(kotlin("stdlib-jdk8"))
   implementation(project(":kotest-assertions"))
   implementation("com.github.tschuchortdev:kotlin-compile-testing:1.2.6")
   testImplementation(project(":kotest-runner:kotest-runner-junit5"))
}

tasks.named<Test>("test") {
   useJUnitPlatform()
   filter {
      isFailOnNoMatchingTests = false
   }
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED, org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED)
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
   }
}

apply(from = "../../publish-jvm.gradle.kts")
