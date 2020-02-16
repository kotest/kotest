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
   id("java")
   kotlin("multiplatform") version Libs.kotlinVersion
   id("java-library")
   id("maven-publish")
   id("com.adarshr.test-logger") version "2.0.0"
   id("org.jetbrains.dokka") version "0.10.1"
}

// apply plugin: "io.kotest"

tasks.named<org.jetbrains.dokka.gradle.DokkaTask>("dokka") {
   outputFormat = "html"
   outputDirectory = "dokka"
}

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
