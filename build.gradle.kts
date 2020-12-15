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
      classpath("com.android.tools.build:gradle:4.1.1")
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
   kotlinOptions.jvmTarget = "1.8"
   kotlinOptions.apiVersion = "1.3"
}

val publications: PublicationContainer = (extensions.getByName("publishing") as PublishingExtension).publications

signing {
   useGpgCmd()
   if (Ci.isRelease)
      sign(publications)
}
