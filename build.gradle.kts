plugins {
   java
   kotlin("multiplatform") version "1.6.21"
   kotlin("plugin.serialization") version "1.6.21" apply false
   id("java-library")
   id("maven-publish")
   signing
   id("com.adarshr.test-logger") version "3.2.0"
}

tasks {
   javadoc {
   }
}

allprojects {
   apply(plugin = "com.adarshr.test-logger")

   repositories {
      mavenCentral()
      mavenLocal()
      maven("https://oss.sonatype.org/content/repositories/snapshots/")
      google()
   }

   group = "io.kotest"
   version = Ci.publishVersion

   tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
      kotlinOptions {
         freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
         jvmTarget = "1.8"
         apiVersion = "1.6"
         languageVersion = "1.6"
      }
   }

   tasks.withType<Test> {
      useJUnitPlatform()
      filter {
         isFailOnNoMatchingTests = false
      }
      testLogging {
         showExceptions = true
         showStandardStreams = true
         events = setOf(
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR,
         )
         exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
      }
   }
}

kotlin {
   targets {
      jvm {
         compilations.all {
            kotlinOptions {
               jvmTarget = "1.8"
            }
         }
      }
   }
}

val publications: PublicationContainer = (extensions.getByName("publishing") as PublishingExtension).publications

signing {
   useGpgCmd()
   if (Ci.isRelease)
      sign(publications)
}

// TODO: Remove me after https://youtrack.jetbrains.com/issue/KT-49109 is fixed
rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
   rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().nodeVersion = "16.0.0"
}
