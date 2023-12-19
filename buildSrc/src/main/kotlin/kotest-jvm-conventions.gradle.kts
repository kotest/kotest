plugins {
   id("kotlin-conventions")
}

kotlin {

   jvm {
      withJava()
   }

   sourceSets {
      val jvmTest by getting {
         dependencies {
            implementation(project(":kotest-runner:kotest-runner-junit5"))
         }
      }
   }
}
