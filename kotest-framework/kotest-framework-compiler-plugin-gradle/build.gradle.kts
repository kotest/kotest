buildscript {
   repositories {
      mavenCentral()
      mavenLocal()
   }
}

plugins {
   id("java")
   id("kotlin")
   id("java-library")
   id("maven-publish")
   id("java-gradle-plugin")
   id("com.gradle.plugin-publish") version "0.15.0"
}

repositories {
   mavenCentral()
   mavenLocal()
}

dependencies {
   compileOnly(gradleApi())
   compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.20")
}

tasks {
   pluginBundle {
      website = "https://kotest.io"
      vcsUrl = "https://github.com/kotest"
      tags = listOf("kotest", "kotlin", "testing", "integrationTesting")
   }
   gradlePlugin {
      plugins {
         create("kotestJsPlugin") {
            id = "io.kotest.js"
            implementationClass = "io.kotest.compiler.gradle.KotestJsPlugin"
            displayName = "Kotest JS Compiler Plugin"
            description = "Adds support to Kotlin for Kotest in Javascript"
         }
      }
   }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
   kotlinOptions.jvmTarget = "1.8"
   kotlinOptions.apiVersion = "1.5"
}
