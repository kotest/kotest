@file:Suppress("UNUSED_VARIABLE")

plugins {
   id("kotlin-conventions")
}

java {
   sourceCompatibility = JavaVersion.VERSION_11
   targetCompatibility = JavaVersion.VERSION_11
}

kotlin {

   targets {
      jvm {
         withJava()

         compilations.all {
            kotlinOptions {
               jvmTarget = "11"
            }
         }
      }
   }

   sourceSets {
      val jvmTest by getting {
         dependencies {
            implementation(project(":kotest-runner:kotest-runner-junit5"))
         }
      }
   }
}
