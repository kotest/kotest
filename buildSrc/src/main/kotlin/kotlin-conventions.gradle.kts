import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
   `java-library`
   kotlin("multiplatform")
   id("com.adarshr.test-logger")
}

testlogger {
   showPassed = false
}

tasks.withType<Test>().configureEach {
   useJUnitPlatform()

   // Tests run in a separate JVM which does not inherit the JVM arguments from the main build:
   // https://docs.gradle.org/current/userguide/java_testing.html
   maxHeapSize = "3G"
   jvmArgs = listOf("-XX:MaxMetaspaceSize=756m", "-XX:+HeapDumpOnOutOfMemoryError", "-Dfile.encoding=UTF-8")

   filter {
      isFailOnNoMatchingTests = false
   }
}

tasks.withType<KotlinCompile>().configureEach {
   kotlinOptions {
      freeCompilerArgs = freeCompilerArgs + listOf(
         "-opt-in=kotlin.RequiresOptIn",
         "-opt-in=io.kotest.common.KotestInternal",
         "-opt-in=io.kotest.common.ExperimentalKotest",
      )
      compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8)
   }
}

kotlin {
   sourceSets.configureEach {
      languageSettings {
         optIn("kotlin.time.ExperimentalTime")
         optIn("kotlin.experimental.ExperimentalTypeInference")
         optIn("kotlin.contracts.ExperimentalContracts")
      }
   }
}

tasks.withType<JavaCompile>().configureEach {
   options.release.set(8)
}
