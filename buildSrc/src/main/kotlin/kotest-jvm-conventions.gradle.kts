@file:Suppress("UNUSED_VARIABLE")

plugins {
   id("kotlin-conventions")
}

kotlin {

   targets {
      jvm {
         withJava()
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
