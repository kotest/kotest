plugins {
   id("kotlin-conventions")
   id("kotest-jvm-conventions")
}

kotlin {
   sourceSets {
      jvmTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(projects.kotestRunner.kotestRunnerJunit5)
         }
      }
   }
}

tasks.withType<Test>().configureEach {
   filter {
      // https://docs.gradle.org/current/javadoc/org/gradle/api/tasks/testing/TestFilter.html
      includeTestsMatching("com.sksamuel.specs.*")
   }
}
