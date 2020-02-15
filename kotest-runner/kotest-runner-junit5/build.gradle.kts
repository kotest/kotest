import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

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
   implementation(project(":kotest-fp"))
   api(project(":kotest-core"))
//            api(project(":kotest-runner:kotest-runner-console"))
   api(Libs.JUnitPlatform.engine)
   api(Libs.JUnitPlatform.api)
   api(Libs.JUnitPlatform.launcher)
   api(Libs.JUnitJupiter.api)

   testImplementation(project(":kotest-core"))
   testImplementation(project(":kotest-assertions"))
   testImplementation(project(":kotest-runner:kotest-runner-junit5"))
   testImplementation(Libs.JUnitPlatform.testkit)
   testImplementation(Libs.Slf4j.api)
   testImplementation(Libs.Logback.classic)
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
      events = setOf(TestLogEvent.FAILED, TestLogEvent.PASSED)
      exceptionFormat = TestExceptionFormat.FULL
   }
}

apply(from = "../../publish-jvm.gradle.kts")
