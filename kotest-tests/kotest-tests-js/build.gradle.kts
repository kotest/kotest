buildscript {
   repositories {
      mavenCentral()
      mavenLocal()
      gradlePluginPortal()
   }
}

plugins {
   kotlin("multiplatform")
   id("io.kotest.multiplatform").version("5.0.0.4")
}

repositories {
   mavenCentral()
   mavenLocal()
   maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

kotlin {
   targets {
      js(IR) {
         browser()
         nodejs()
      }
   }

   sourceSets {
      val commonTest by getting {
         dependencies {
            implementation(kotlin("stdlib"))
            implementation(project(Projects.AssertionsCore))
            implementation("io.kotest:kotest-framework-engine:5.0.0.399-SNAPSHOT")
         }
      }
   }
}

apply(from = "../../nopublish.gradle")
