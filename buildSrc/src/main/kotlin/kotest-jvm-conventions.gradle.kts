@file:Suppress("UNUSED_VARIABLE")

plugins {
   id("kotlin-conventions")
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
