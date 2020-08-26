buildscript {

   repositories {
      mavenCentral()
      mavenLocal()
      google()
      maven("https://dl.bintray.com/kotlin/kotlin-eap")
      maven("https://kotlin.bintray.com/kotlinx")
      gradlePluginPortal()
   }
}

plugins {
   java
   kotlin("multiplatform") version Libs.kotlinVersion
   id("java-library")
   id("maven-publish")
   signing
   id("com.adarshr.test-logger") version Libs.adarshrTestLoggerVersion
   id("org.jetbrains.dokka") version Libs.dokkaVersion
   id("io.kotest") version Libs.kotestGradlePlugin

   // To get versions report, execute:
   // Win: .\gradlew.bat dependencyUpdates -Drevision=release
   // Other: ./gradlew dependencyUpdates -Drevision=release
   id("com.github.ben-manes.versions") version Libs.gradleVersionsPluginVersion
}

tasks {
   javadoc {
   }
}

// Configure existing Dokka task to output HTML to typical Javadoc directory
//tasks.dokka {
//   outputFormat = "html"
//   outputDirectory = "$buildDir/javadoc"
//   configuration {
//      includeNonPublic = false
//      skipDeprecated = true
//      reportUndocumented = false
//      skipEmptyPackages = true
//      targets = listOf("JVM")
//      platform = "JVM"
//      jdkVersion = 8
//   }
//}

// apply plugin: "io.kotest"

allprojects {

   repositories {
      mavenCentral()
      jcenter()
      google()
      maven("https://dl.bintray.com/kotlin/kotlin-eap")
      maven("https://kotlin.bintray.com/kotlinx")
   }

   group = "io.kotest"
   version = Ci.publishVersion
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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
   kotlinOptions.jvmTarget = "1.8"
   kotlinOptions.apiVersion = "1.3"
}

val publications: PublicationContainer = (extensions.getByName("publishing") as PublishingExtension).publications

signing {
   useGpgCmd()
   if (Ci.isRelease)
      sign(publications)
}
