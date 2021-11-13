buildscript {

   repositories {
      mavenCentral()
      mavenLocal()
      google()
      gradlePluginPortal()
   }

   dependencies {
      // To be uncommented if adding any Android project
      classpath("com.android.tools.build:gradle:7.0.3")
   }
}

plugins {
   java
   kotlin("multiplatform")
   id("java-library")
   id("maven-publish")
   signing
   id("com.adarshr.test-logger")
   id("org.jetbrains.dokka")
}

tasks {
   javadoc {
   }
}

allprojects {

   repositories {
      mavenCentral()
      mavenLocal()
      maven("https://oss.sonatype.org/content/repositories/snapshots/")
      google()
      maven {
         url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
      }
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
