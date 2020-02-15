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
   implementation(project(":kotest-core"))
   implementation(kotlin("stdlib-jdk8"))
   implementation(kotlin("reflect"))
   implementation(Libs.Allure.commons)
   implementation("javax.xml.bind:jaxb-api:2.3.1")
   implementation("com.sun.xml.bind:jaxb-core:2.3.0.1")
   implementation("com.sun.xml.bind:jaxb-impl:2.3.2")
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
      events = setOf(
         org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
         org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
      )
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
   }
}

apply(from = "../../publish-jvm.gradle.kts")
