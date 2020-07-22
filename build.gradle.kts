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

   // To get versions report, execute:
   // Win: .\gradlew.bat dependencyUpdates -Drevision=release
   // Other: gradle dependencyUpdates -Drevision=release
   id("com.github.ben-manes.versions") version Libs.gradleVersionsPluginVersion
}

// Configure existing Dokka task to output HTML to typical Javadoc directory
tasks.dokka {
   outputFormat = "html"
   outputDirectory = "$buildDir/javadoc"
}

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

val publications: PublicationContainer = (extensions.getByName("publishing") as PublishingExtension).publications

signing {
   useGpgCmd()
   if (Ci.isRelease)
      sign(publications)
}
