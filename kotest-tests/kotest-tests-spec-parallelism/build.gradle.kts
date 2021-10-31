plugins {
   id("java")
   id("kotlin-multiplatform")
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

   sourceSets {

      val commonMain by getting {
         dependencies {
            compileOnly(kotlin("stdlib"))
            implementation(kotlin("reflect"))
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.JunitRunner))
            implementation(Libs.Coroutines.coreJvm)
         }
      }
   }
}

apply(from = "../../nopublish.gradle")
