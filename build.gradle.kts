buildscript {

   repositories {
      mavenCentral()
      mavenLocal()
      google()
      maven("https://dl.bintray.com/kotlin/kotlin-eap")
      maven("https://kotlin.bintray.com/kotlinx")
      gradlePluginPortal()
   }

   dependencies {
      // To be uncommented if adding any Android project
      classpath("com.android.tools.build:gradle:4.2.1")
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

// apply plugin: "io.kotest"

allprojects {

//   apply(plugin = "io.kotest")

   repositories {
      mavenCentral()
      jcenter()
      google()
      maven("https://kotlin.bintray.com/kotlinx")
      maven("https://dl.bintray.com/kotlin/kotlin-eap")
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
   kotlinOptions {
      freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
      freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.time.ExperimentalTime"
      jvmTarget = "1.8"
      apiVersion = "1.5"
   }

}

val publications: PublicationContainer = (extensions.getByName("publishing") as PublishingExtension).publications

signing {
   useGpgCmd()
   if (Ci.isRelease)
      sign(publications)
}
