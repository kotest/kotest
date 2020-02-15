plugins {
   id("java")
   kotlin("jvm")
   id("java-library")
   id("com.adarshr.test-logger")
}

repositories {
   mavenCentral()
}

dependencies {
   implementation(kotlin("stdlib-jdk8"))
   implementation(kotlin("reflect"))
   api(project(":kotest-assertions"))
   api(project(":kotest-property"))
   implementation("io.arrow-kt:arrow-core:0.10.4")
   testImplementation(project(":kotest-runner:kotest-runner-junit5"))
}

tasks.named<Test>("test") {
   useJUnitPlatform()
   filter {
      setFailOnNoMatchingTests(false)
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

apply(from = "../../publish-jvm.gradle.kts")
