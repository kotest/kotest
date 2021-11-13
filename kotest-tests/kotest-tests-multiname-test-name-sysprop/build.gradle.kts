plugins {
   id("java")
   kotlin("multiplatform")
   id("java-library")
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

   targets.all {
      compilations.all {
         kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
         }
      }
   }

   sourceSets {

      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Assertions.Shared))
            implementation(project(Projects.JunitRunner))
            implementation(Libs.Coroutines.coreJvm)
         }
      }
   }
}

apply(from = "../../nopublish.gradle")
