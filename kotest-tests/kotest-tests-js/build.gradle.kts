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
            implementation(project(Projects.Framework.engine))
         }
      }
   }
}

apply(from = "../../nopublish.gradle")
