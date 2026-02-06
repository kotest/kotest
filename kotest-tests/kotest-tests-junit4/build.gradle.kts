import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
   id("kotest-junit4-conventions")
}

kotlin {
   sourceSets {
      jvmTest {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }
   }
}

tasks.withType<Test>().configureEach {
   outputs.upToDateWhen { false }
   testLogging {
      events(TestLogEvent.FAILED, TestLogEvent.STANDARD_OUT, TestLogEvent.STANDARD_ERROR)
   }
}
