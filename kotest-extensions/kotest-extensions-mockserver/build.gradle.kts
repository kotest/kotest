import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
   api("org.mock-server:mockserver-netty:5.9.0")
   api("org.mock-server:mockserver-client-java:5.9.0")

   testImplementation(project(":kotest-runner:kotest-runner-junit5"))
}

tasks.withType<KotlinCompile>().configureEach {
   kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
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
