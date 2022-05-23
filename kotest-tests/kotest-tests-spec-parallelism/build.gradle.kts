plugins {
   kotlin("multiplatform")
}

kotlin {

   targets {
      jvm()
   }

   sourceSets {
      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.JunitRunner))
         }
      }
   }
}

apply(from = "../../nopublish.gradle")
