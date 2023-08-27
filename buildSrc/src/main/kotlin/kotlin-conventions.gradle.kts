import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
   kotlin("multiplatform")
   id("com.adarshr.test-logger")
}

repositories {
   mavenCentral()
   mavenLocal()
   maven("https://oss.sonatype.org/content/repositories/snapshots/")
   google()
   gradlePluginPortal() // tvOS builds need to be able to fetch a kotlin gradle plugin
}

testlogger {
   showPassed = false
}

tasks.withType<Test>().configureEach {
   useJUnitPlatform()
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
      apiVersion = "1.8"
      languageVersion = "1.8"
      compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
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
   options.release.set(11)
}
