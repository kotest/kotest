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
   api(kotlin("reflect"))
   implementation(project(":kotest-fp"))
   api(project(":kotest-core"))
   api(project(":kotest-extensions"))
   api(Libs.Coroutines.core)
   api(Libs.Classgraph.classgraph)
   testImplementation(project(":kotest-runner:kotest-runner-junit5"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
   kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
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
      events = setOf(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED, org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED)
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
   }
}

apply(from = "../../publish-jvm.gradle.kts")
