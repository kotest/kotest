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
   id("com.gradle.plugin-publish") version "0.16.0"
}

version = Ci.gradleVersion

java {
   sourceCompatibility = JavaVersion.VERSION_1_8
   targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
   mavenCentral()
   mavenLocal()
}

dependencies {
   compileOnly(gradleApi())
   compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.20-dev-4660")
}

tasks {
   pluginBundle {
      website = "https://kotest.io"
      vcsUrl = "https://github.com/kotest"
      tags = listOf("kotest", "kotlin", "testing", "integrationTesting", "javascript")
   }
   gradlePlugin {
      plugins {
         create("KotestMultiplatformCompilerGradlePlugin") {
            id = "io.kotest.multiplatform"
            implementationClass = "io.kotest.framework.multiplatform.gradle.KotestMultiplatformCompilerGradlePlugin"
            displayName = "Kotest Multiplatform Compiler Plugin"
            description = "Adds support for Javascript and Native tests in Kotest"
         }
      }
   }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
   kotlinOptions.jvmTarget = "1.8"
   kotlinOptions.apiVersion = "1.5"
}
