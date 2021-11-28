buildscript {
   repositories {
      mavenCentral()
      mavenLocal()
      gradlePluginPortal()
   }
}

plugins {
   kotlin("multiplatform")
   // must be a published version and not one in the current build
   id("io.kotest.multiplatform").version("5.0.0")
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
            implementation(project(Projects.Assertions.Core))
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Framework.datatest))
         }
      }
   }
}

// must be a published version and not one in the current build
configure<io.kotest.framework.multiplatform.gradle.KotestPluginExtension> {
   compilerPluginVersion.set("5.0.0")
}

apply(from = "../../nopublish.gradle")
