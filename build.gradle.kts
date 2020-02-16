import Ci.isGithub

buildscript {

   repositories {
      mavenCentral()
      mavenLocal()
   }

   dependencies {
      classpath("com.android.tools.build:gradle:3.5.3")
      // classpath "io.kotest:kotest-gradle-plugin:1.1.1-LOCAL"
   }

   repositories {
      mavenCentral()
      google()
   }
}

plugins {
   java
   kotlin("multiplatform") version Libs.kotlinVersion
   id("java-library")
   id("maven-publish")
   signing
   id("com.adarshr.test-logger") version "2.0.0"
   id("org.jetbrains.dokka") version "0.10.1"
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
   }

   group = "io.kotest"

   if (isGithub) {
      version = "4.0.0." + Ci.githubBuildNumber + "-SNAPSHOT"
   }
}

val publications: PublicationContainer = (extensions.getByName("publishing") as PublishingExtension).publications

signing {
   useGpgCmd()
   sign(publications)
}
