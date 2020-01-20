plugins {
   id("java")
   id("kotlin-multiplatform")
   id("java-library")
}

repositories {
   mavenCentral()
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
            freeCompilerArgs + "-Xuse-experimental=kotlin.Experimental"
         }
      }
   }

   sourceSets {
      val jvmMain by getting {
         dependencies {
            api(kotlin("stdlib-jdk8"))
         }
      }
      val jvmTest by getting {
         api(project(":kotest-runner:kotest-runner-junit5"))
      }
   }
}
