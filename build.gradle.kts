buildscript {

   repositories {
      mavenCentral()
      mavenLocal()
   }

   dependencies {
      classpath("org.jetbrains.dokka:dokka-gradle-plugin:0.10.0")
      classpath("com.android.tools.build:gradle:3.5.0")
      //    classpath "io.kotest:kotest-gradle-plugin:1.1.1-LOCAL"
   }

   repositories {
      mavenCentral()
      google()
   }
}

plugins {
   java
   `java-library`
   kotlin("multiplatform") version Libs.kotlinVersion
   id("org.jetbrains.dokka") version Libs.dokkaVersion
   `maven-publish`
   signing
//   id 'net.researchgate.release' version '2.8.0'
}

allprojects {

   apply(plugin = "org.jetbrains.dokka")

   repositories {
      mavenCentral()
      jcenter()
      google()
   }

   group = "io.kotest"

   if (Travis.isTravis) {
      version = "4.0.${Travis.travisBuildNumber}-SNAPSHOT"
   }

   val dokka by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class) {
      outputFormat = "html"
      outputDirectory = "$buildDir/dokka"
      impliedPlatforms = arrayListOf("Common") // This will force platform tags for all non-common sources e.g. "JVM"
//      kotlinTasks {
//         // dokka fails to retrieve sources from MPP-tasks so they must be set empty to avoid exception
//         // use sourceRoot instead (see below)
//         arrayListOf(project.compileKotlinJvm)
//      }
//      sourceRoot {
//         // assuming there is only a single source dir...
//         path = kotlin.sourceSets.commonMain.kotlin.srcDirs[0]
//         platforms = arrayListOf("Common")
//      }
//      sourceRoot {
//         // assuming there is only a single source dir...
//         path = kotlin.sourceSets.jvmMain.kotlin.srcDirs[0]
//         platforms = arrayListOf("JVM")
//      }
   }

   val dokkaJar by tasks.creating(Jar::class) {
      group = JavaBasePlugin.DOCUMENTATION_GROUP
      description = "Assembles Kotlin docs with Dokka"
      archiveClassifier.set("javadoc")
      from(tasks.dokka)
      dependsOn(tasks.dokka)
   }

//   publishing {
//      repositories {
//         maven {
//
//            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
//            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
//
//            val ossrhUsername: String by project
//            val ossrhPassword: String by project
//
//            name = "deploy"
//            url = if (Travis.isTravis) snapshotsRepoUrl else releasesRepoUrl
//            credentials {
//               username = System.getenv("OSSRH_USERNAME") ?: ossrhUsername
//               password = System.getenv("OSSRH_PASSWORD") ?: ossrhPassword
//            }
//         }
//      }
//   }

//   release {
//      failOnCommitNeeded = false
//      failOnPublishNeeded = false
//      failOnUnversionedFiles = false
//   }

   //afterReleaseBuild.dependsOn publish
}

apply(from = "./publish.gradle.kts")
