import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
   kotlin("multiplatform")
   id("com.adarshr.test-logger")
}

group = "io.kotest"
version = Ci.publishVersion

repositories {
   mavenCentral()
   mavenLocal()
   maven("https://oss.sonatype.org/content/repositories/snapshots/")
   google()
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
