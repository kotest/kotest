import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

tasks.withType<Test>() {
   useJUnitPlatform()

   filter {
      isFailOnNoMatchingTests = false
   }
}

tasks.withType<KotlinCompile>().configureEach {
   kotlinOptions {
      freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
      jvmTarget = "1.8"
      apiVersion = "1.6"
      languageVersion = "1.6"
   }
}

kotlin {
   sourceSets {
      all {
         languageSettings.optIn("kotlin.time.ExperimentalTime")
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
         languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
      }
   }
}
