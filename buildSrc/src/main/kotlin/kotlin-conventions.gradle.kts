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
   // Pass "kotest.*" system properties from the Gradle invocation to the test launcher.
   // https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params
   for ((name, value) in System.getProperties()) {
      if (name is String && name.startsWith("kotest.")) {
         value as String
         inputs.property(name, value)
         systemProperty(name, value)
      }
   }
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
