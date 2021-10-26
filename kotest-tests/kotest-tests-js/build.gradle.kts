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
   id("io.kotest.multiplatform").version("5.0.0.5")
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
            implementation(project(Projects.Assertions.Core))
            implementation(project(Projects.Framework.engine))
            // not sure why this is required for the js tests to appear
            implementation(project(Projects.Framework.datatest))
         }
      }
   }
}

// must be a published version and not one in the current build
//configure<io.kotest.framework.multiplatform.gradle.KotestPluginExtension> {
//   compilerPluginVersion.set("5.0.0.399-SNAPSHOT")
//}

apply(from = "../../nopublish.gradle")
